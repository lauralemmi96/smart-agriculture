#include "contiki.h"
#include "coap-engine.h"
#include <string.h>

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "Humidity"
#define LOG_LEVEL LOG_LEVEL_APP

int min_value =	20;
int max_value =	80;
int humidity_value = 10;

static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_event_handler(void);

/*----------------------------------------------------------------------------------------------*/

EVENT_RESOURCE(res_humidity,
         "title=\"Humidity Sensor\";rt=\"humidity\";if=\"sensor\";obs",
	res_get_handler,
         NULL,
         NULL,
         NULL, 
	res_event_handler);
        
/*----------------------------------------------------------------------------------------------*/

static void res_event_handler(void) {
 
    humidity_value = (rand() % (max_value - min_value + 1)) + min_value;
    coap_notify_observers(&res_humidity);
}


/* Humidity sensor: given the area, it returns the humudity percentage detected */


static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){

	
	coap_set_header_content_format(response, TEXT_PLAIN);
	coap_set_payload(response, buffer, snprintf((char *)buffer, preferred_size, "HUMIDITY: %i\n", humidity_value));
	

}
