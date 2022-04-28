#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "contiki.h"
#include "coap-engine.h"
#include "sys/etimer.h"

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "App"
#define LOG_LEVEL LOG_LEVEL_APP

/*	Declare external resources to be activated	*/
extern coap_resource_t res_humidity;

/*	Declare timer	*/
static struct etimer e_timer;

/* Declare and auto-start this file's process */
PROCESS(device_process, "Sensor/Actuator Device");
AUTOSTART_PROCESSES(&device_process);


/*------------------------------------------------------------------------------------------*/

PROCESS_THREAD(device_process, ev, data){

	PROCESS_BEGIN();
	PROCESS_PAUSE();

	coap_activate_resource(&res_humidity, "humidity");

	etimer_set(&e_timer, CLOCK_SECOND * 4);

	printf("Periodical request of humidity level\n");

	while(1){

		PROCESS_WAIT_EVENT();
		if(ev == PROCESS_EVENT_TIMER && data == &e_timer){
		    printf("Event triggered\n");
		  
			res_humidity.trigger();
			
			etimer_set(&e_timer, CLOCK_SECOND * 4);
		}
	}
  	PROCESS_END();
}
