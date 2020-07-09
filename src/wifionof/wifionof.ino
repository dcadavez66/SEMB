#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WebServer.h>
#include <SimpleDHT.h>
#include <WiFiUdp.h>
#include <NTPClient.h>

#define NUMBER_OF_TASKS 3
#define HEATER 0
#define LAMP 1
#define Charger 2
#define NUMBER_OF_PLUGS 3

String ssid = "";
String password = "";
boolean domesticWifi = false;
const char* local_network = "ESP_SERVER";
const char* pass = "";
byte temperature = 0;
byte humidity = 0;
int current_hours = -1; 
int current_minutes = -1;
String IP = "";



WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org");



typedef struct {
  boolean TemperatureControl = false; // when true enables the temperature control
  boolean TimeControl = false; // when true enables the time control
  int minTemperature = 0; 
  int maxTemperature = 0;
  int turnOnHours = 0;
  int turnOnMinutes = 0;
  int turnOffHours = 0;
  int turnOffMinutes = 0;
  int OutPin = 0;
  int plugType = -1; // type of device connected to the plug
} PLUG;

PLUG plug[3];



typedef struct {
  /* period in ticks */
  int period;
  /* ticks until next activation */
  int delays;
  /* function pointer */
  void (*func)(void);
  /* activation counter */
  int exec;
} Sched_Task_t;


Sched_Task_t Tasks[NUMBER_OF_TASKS];

ESP8266WebServer server(80);

SimpleDHT11 dht11;
int pinDHT11 = 16;



/*****************************************************************************************************************************************************************/
       // Task Manager Functions 
/*****************************************************************************************************************************************************************/

int Sched_Init(void){

  byte x;
  for(x=0; x < NUMBER_OF_TASKS; x++){
    Tasks[x].func = 0;
  }
}


int Sched_AddT(void (*f)(void),int d, int p){
  byte x;
 for(x=0; x<NUMBER_OF_TASKS; x++)
 if (!Tasks[x].func) {
 Tasks[x].period = p;
 Tasks[x].delays = d;
 Tasks[x].exec = 0; 
 Tasks[x].func = f;
 return x;
 }
 return -1; // if no free TCB
  
}


 
void Sched_Schedule(void){

 
 byte x;
  for(x=0; x<NUMBER_OF_TASKS; x++) {
     if((Tasks[x].func) && (Tasks[x].delays)){
         Tasks[x].delays--;
 
         if(!Tasks[x].delays){
   
             /* Schedule Task */
             Tasks[x].exec=1; // if overrun, following ativation is lost
             Tasks[x].delays = Tasks[x].period; //reset counter
         }
    }
  }

  return;
}



void Sched_Dispatch(void){


  
  byte curr_task = NUMBER_OF_TASKS;
  byte x;
  for(x=0; x<curr_task; x++) {
   
    if((Tasks[x].func)&&(Tasks[x].exec)) {
        Tasks[x].exec--;
        Tasks[x].func();
        /* Delete task
         * if one-shot
        */
       if(!Tasks[x].period){
          Tasks[x].func = 0;
       }

   
      return;
   }
 }
 
}

/******************************************************************************************************************************************************************/


//sends ESP IP
void handleRoot() {
  server.send(200, "text/plain" , IP);
}

//handles connection to the domestic network
void handleConfig() {


  if (server.hasArg("USERNAME") && server.hasArg("PASSWORD")) {
    
    
    ssid = server.arg("USERNAME");
    password = server.arg("PASSWORD");
    Serial.println(ssid);
    Serial.println(password);

    WiFi.begin(ssid , password);
    delay(500);
    int n = 0;
    while (WiFi.status() != WL_CONNECTED && n < 10 ) {
      delay(500);
      Serial.print(".");
      n++;
    }

    if (WiFi.status() != WL_CONNECTED ) {
      Serial.println("Error connecting");
      server.send(400 , "text/plain" , "Error connecting");
    }
    else if (WiFi.status() == WL_CONNECTED) {
      Serial.println("Connection was Successful.");
      server.send(200 , "text/plain" , WiFi.localIP().toString());
      domesticWifi = true;
    }

    return;
  }
}

void plugSetup() {
  int number = -1;
  int type = -1;
  int auxMinTemperature = 0;
  int auxMaxTemperature = 0;
  int auxOnHours = 0;
  int auxOnMinutes = 0;
  int auxOffHours = 0;
  int auxOffMinutes = 0;
  boolean auxTemperatureControl = false;
  boolean auxTimeControl = false;

  if (server.hasArg("PLUGNUMBER") && server.hasArg("PLUGTYPE")) {

    number = server.arg("PLUGNUMBER").toInt();
    type = server.arg("PLUGTYPE").toInt();

    //conditions to verify the plug type, and what actions need to be handled
    if ( type == HEATER) {

      if (server.arg("TEMPERATUREMIN").toInt() != -1) {
        auxMinTemperature = server.arg("TEMPERATUREMIN").toInt();
      }

      if (server.arg("TEMPERATUREMAX").toInt() != -1) {
        auxMaxTemperature = server.arg("TEMPERATUREMAX").toInt();
      }

      if (auxMinTemperature != 0 || auxMaxTemperature != 0) {
        auxTemperatureControl = true;
      }

      if (server.arg("ONHOURS").toInt() != -1) {
        auxOnHours = server.arg("ONHOURS").toInt();
      }

      if (server.arg("ONMINUTES").toInt() != -1) {
        auxOnMinutes = server.arg("ONMINUTES").toInt();
      }

      if (server.arg("OFFHOURS").toInt() != -1) {
        auxOffHours = server.arg("OFFHOURS").toInt();
      }

      if (server.arg("OFFMINUTES").toInt() != -1) {
        auxOffMinutes = server.arg("OFFMINUTES").toInt();
      }

      if (auxOnHours != 0 || auxOnMinutes != 0 || auxOffHours != 0 || auxOffMinutes != 0) {
        auxTimeControl = true;
      }

    }

    else if (type == LAMP) {

      auxOnHours = server.arg("ONHOURS").toInt();
      auxOnMinutes = server.arg("ONMINUTES").toInt();

      auxOffHours = server.arg("OFFHOURS").toInt();
      auxOffMinutes = server.arg("OFFMINUTES").toInt();

      if (server.arg("ONHOURS").toInt() != -1) {
        auxOnHours = server.arg("ONHOURS").toInt();
      }

      if (server.arg("ONMINUTES").toInt() != -1) {
        auxOnMinutes = server.arg("ONMINUTES").toInt();
      }

      if (server.arg("OFFHOURS").toInt() != -1) {
        auxOffHours = server.arg("OFFHOURS").toInt();
      }

      if (server.arg("OFFMINUTES").toInt() != -1) {
        auxOffMinutes = server.arg("OFFMINUTES").toInt();
      }

      if (auxOnHours != 0 || auxOnMinutes != 0 || auxOffHours != 0 || auxOffMinutes != 0) {
        auxTimeControl = true;
      }
    }

    else if ( type == Charger) {

    }
    //set plug values
    plug[number].maxTemperature = auxMaxTemperature;
    Serial.print("Plug[");
    Serial.print(number);
    Serial.print("].");
    Serial.print("maxTemperature ");
    Serial.println(plug[number].maxTemperature);
    Serial.println("");
    plug[number].minTemperature = auxMinTemperature;
    Serial.print("Plug[");
    Serial.print(number);
    Serial.print("].");
    Serial.print("minTemperature ");
    Serial.println(plug[number].minTemperature);
    Serial.println("");
    plug[number].turnOnHours = auxOnHours;
    Serial.print("Plug[");
    Serial.print(number);
    Serial.print("].");
    Serial.print("turnOnHours ");
    Serial.println(plug[number].turnOnHours);
    Serial.println("");
    plug[number].turnOnMinutes = auxOnMinutes;
    Serial.print("Plug[");
    Serial.print(number);
    Serial.print("].");
    Serial.print("turnOnMinutes");
    Serial.println(plug[number].turnOnMinutes);
    Serial.println("");
    plug[number].turnOffHours = auxOffHours;
    Serial.print("Plug[");
    Serial.print(number);
    Serial.print("].");
    Serial.print("turnOffHours ");
    Serial.println(plug[number].turnOffHours);
    Serial.println("");
    plug[number].turnOffMinutes = auxOffMinutes;
    Serial.print("Plug[");
    Serial.print(number);
    Serial.print("].");
    Serial.print("turnOffMinutes ");
    Serial.println(plug[number].turnOffMinutes );
    Serial.println("");
    plug[number].TemperatureControl = auxTemperatureControl;
    plug[number].TimeControl = auxTimeControl;

  }
}


void turnON() {
  int number = -1;
  if (server.hasArg("PLUGNUMBER")) {
    number = server.arg("PLUGNUMBER").toInt();

    digitalWrite(plug[number].OutPin, HIGH);
    server.send(200, "text/plain", " ON");
    Serial.print("Plug[");
    Serial.print(number);
    Serial.print("]");
    Serial.println("HIGH");
  }
}

void turnOFF() {
  int number = -1;
  if (server.hasArg("PLUGNUMBER")) {
    number = server.arg("PLUGNUMBER").toInt();

    digitalWrite(plug[number].OutPin, LOW);
    server.send(200, "text/plain", " OFF");
    Serial.print("Plug[");
    Serial.print(number);
    Serial.print("]");
    Serial.println("LOW");
  }
}

void auto_turn_on(int number)
{
  digitalWrite(plug[number].OutPin, HIGH);
  
  Serial.print("Plug[");
  Serial.print(number);
  Serial.print("]");
  Serial.println("HIGH ");
  
  return;
}

void auto_turn_off(int number){
  
  digitalWrite(plug[number].OutPin, LOW);

  Serial.print("Plug[");
  Serial.print(number);
  Serial.print("]");
  Serial.println("LOW");

  return;
}

void get_time(){
  timeClient.update();

  current_hours = timeClient.getHours();
  Serial.println("**");
  Serial.print(current_hours);
  Serial.print(":");
  current_minutes = timeClient.getMinutes();
  Serial.println(current_minutes);
  Serial.print("**");
  return; 
}

void get_temp(){

  //temperature = DHT.temperature;
  dht11.read(pinDHT11 , &temperature , &humidity , NULL);
  Serial.print("Temperature:");
  Serial.println(temperature);

  return;
}

void check_temp(int i){


    if( plug[i].minTemperature == temperature){
          auto_turn_on(i);
    }
    else if(plug[i].maxTemperature == temperature){
         auto_turn_off(i);
    }

  

  return;
}

void check_time(int i){
 
  if( plug[i].turnOnHours==current_hours &&  plug[i].turnOnMinutes == current_minutes){
        auto_turn_on(i);
  }
  else if(plug[i].turnOffHours==current_hours &&  plug[i].turnOffMinutes == current_minutes){
       auto_turn_off(i);
  }
  return;
}

void time_is_up(){

  int i=0;
  
  while(i<NUMBER_OF_PLUGS){
    if(plug[i].TimeControl==true){
      check_time(i);
    }
    i++;
  }
  
  return;
}

void temp_is_up(){

  int i=0;

  while(i<NUMBER_OF_PLUGS){
    if(plug[i].TemperatureControl==true){
      check_temp(i);
    }
    i++;
  }

  return;
}

void resetT() {
  int number = -1;
  if (server.hasArg("PLUGNUMBER")) {
    number = server.arg("PLUGNUMBER").toInt();

    plug[number].maxTemperature = 0;
    Serial.print("Plug[");
    Serial.print(number);
    Serial.print("].");
    Serial.print("maxTemperature:");
    Serial.println(plug[number].maxTemperature );
    plug[number].minTemperature = 0;
    Serial.print("Plug[");
    Serial.print(number);
    Serial.print("].");
    Serial.print("minTemperature:");
    Serial.println(plug[number].minTemperature );
    plug[number].TemperatureControl = false;

    plug[number].turnOnHours = 0;
    Serial.print("Plug[");
    Serial.print(number);
    Serial.print("].");
    Serial.print("OnHours:");
    Serial.println(plug[number].turnOnHours);
    plug[number].turnOnMinutes = 0;
    Serial.print("Plug[");
    Serial.print(number);
    Serial.print("].");
    Serial.print("OnMinutes:");
    Serial.println(plug[number].turnOnMinutes);
    plug[number].turnOffHours = 0;
    Serial.print("Plug[");
    Serial.print(number);
    Serial.print("].");
    Serial.print("OffHours:");
    Serial.println(plug[number].turnOffHours);
    plug[number].turnOffMinutes = 0;
    Serial.print("Plug[");
    Serial.print(number);
    Serial.print("].");
    Serial.print("OffMinutes:");
    Serial.println(plug[number].turnOffMinutes);

  }
  return;
}

void callClientHandler(){
   
    
    Serial.println("running client Handler!");
    server.handleClient();
    Serial.println("STOPPED client Handler!");
   
    return;
}
void callTimeHandler(){

    Serial.println("########running TIME Handler!########");
    if(domesticWifi == true && (plug[0].TimeControl == true || plug[1].TimeControl == true || plug[2].TimeControl == true)){
      Serial.print("####tirar medida de tempo######");
      get_time();
      //delay(500);
      time_is_up();
     
    }
    
    return;
}

void callTemperatureHandler(){
  
  
  Serial.println("!!!!!!!running TEMPERATURE Handler!!!!!!");
  if(domesticWifi == true && ( plug[0].TemperatureControl == true || plug[1].TemperatureControl == true || plug[2].TemperatureControl == true )){
    Serial.print("!!!!tirar medida de temperatura!!!!");
    get_temp();
    //delay(500);
    temp_is_up();
  }
  else{
    
  }
  return;
}

void ICACHE_RAM_ATTR onTimerISR(){
    noInterrupts();
    
    Sched_Schedule();  
    timer1_write(600000);//120ms
    interrupts();

    return;
}

void setup() {
  plug[0].OutPin = 2;
  plug[1].OutPin = 0;
  plug[2].OutPin = 4;
  pinMode(plug[0].OutPin, OUTPUT);
  pinMode(plug[1].OutPin , OUTPUT);
  pinMode(plug[2].OutPin , OUTPUT);
  Serial.begin(115200); // Initialize the serial bus with a 115200 baud rate. This will allow us to send data back to the computer through the USB cable
  timeClient.begin(); // Initialize a NTPClient to get time
  timeClient.setTimeOffset(3600); //estamos em Portugal- offset =+1
  WiFi.softAP(local_network, pass);
  Serial.print("--->");
  Serial.print(local_network);
  Serial.println("IS ON.");
  IP = WiFi.softAPIP().toString();

  Sched_Init();
  Sched_AddT(callClientHandler,1,1); // add task1 initial delay:120ms , period: 120ms
  Sched_AddT(callTimeHandler,50,50); // add task2 initial delay: 6s , period: 6s
  Sched_AddT(callTemperatureHandler ,100,100); //add task3 initial delay: 12s , period: 12s
  
  noInterrupts();
  
    timer1_attachInterrupt(onTimerISR);
    timer1_enable(TIM_DIV16, TIM_EDGE, TIM_SINGLE);
    timer1_write(600000); //120 ms
    
    
 interrupts(); // enable all interrupts  
  
  server.on("/" , handleRoot);
  server.on("/on" , turnON);
  server.on("/off", turnOFF);
  server.on("/config", handleConfig);
  server.on("/plugSetup" , plugSetup );
  server.on("/ResetT" , resetT);
  server.begin();// Start the server
  

}

void loop() {

  Sched_Dispatch();
}
