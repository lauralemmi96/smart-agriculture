#include "contiki.h"
#include "coap-engine.h"
#include <string.h>
#include "dev/leds.h"

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "Sprinkler"
#define LOG_LEVEL LOG_LEVEL_APP

bool status = false;

static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_post_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_event_handler(void);

/*----------------------------------------------------------------------------------------------*/

EVENT_RESOURCE(res_sprinkler,
         "title=\"Humidity Sensor\";rt=\"humidity\";if=\"sensor\";obs",
	res_get_handler,
         res_post_put_handler,
         res_post_put_handler,
         NULL, 
	res_event_handler);
        
/*----------------------------------------------------------------------------------------------*/

static void res_event_handler(void) {
 
    coap_notify_observers(&res_sprinkler);
}


/* Sprinkler Activator: it returns its status */


static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){

	
	coap_set_header_content_format(response, TEXT_PLAIN);
	coap_set_payload(response, buffer, snprintf((char *)buffer, preferred_size, "SPRINKLER STATUS: %s\n", status ? "ON" : "OFF"));
	

}


/* Sprinkler Activator: it changes its status, and the leds accordingly RED: OFF, GREEN: ON */

static void res_post_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){

	if(request == NULL){

		LOG_INFO("[SPRINKLER]: Empty request\n");
		return;
	}

	size_t len = 0;
	const char *new_status = NULL;
	bool good_req = false;

	if((len = coap_get_query_variable(request, "status", &new_status))) {
		if(strncmp(new_status, "ON", len) == 0) {
			good_req = true;
			status = true;
			leds_set(LEDS_NUM_TO_MASK(LEDS_GREEN));
			LOG_DBG("Sprinkler switched ON\n");		
		}else if(strncmp(new_status, "OFF", len) == 0) {
			good_req = true;
			status = false;
			leds_set(LEDS_NUM_TO_MASK(LEDS_RED));
			LOG_DBG("Sprinkler switched OFF\n");
		}	
	}

	if(!good_req)
		coap_set_status_code(response, BAD_REQUEST_4_00);


}
