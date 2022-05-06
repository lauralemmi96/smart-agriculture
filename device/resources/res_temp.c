#include "contiki.h"
#include "coap-engine.h"
#include <string.h>
#include <stdlib.h>
#include <ctype.h>

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "Temperature"
#define LOG_LEVEL LOG_LEVEL_APP

int min_temp_value =	5;
int max_temp_value =	25;
int temp_value = 10;
static unsigned int accept = -1;

static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_post_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_event_handler(void);

/*----------------------------------------------------------------------------------------------*/

EVENT_RESOURCE(res_temp,
         "title=\"Temperature Sensor\";rt=\"temperature\";if=\"sensor\";obs",
	res_get_handler,
        res_post_put_handler,
        res_post_put_handler,
         NULL, 
	res_event_handler);
        
/*----------------------------------------------------------------------------------------------*/

static void res_event_handler(void) {
 
    coap_notify_observers(&res_temp);
}


/* Temperature sensor: given the area, it returns the humudity percentage detected */


static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
	
	char response_message[COAP_MAX_CHUNK_SIZE];
	temp_value = (rand() % (max_temp_value - min_temp_value + 1)) + min_temp_value;

	coap_get_header_accept(request, &accept);
	
	if(accept == APPLICATION_JSON){

		coap_set_header_content_format(response, APPLICATION_JSON);
		
		int len = snprintf(response_message, COAP_MAX_CHUNK_SIZE, "{\"temperature\":%d}", temp_value);
		if(len > 0){
			memcpy(buffer, (uint8_t*)response_message, len);
            		coap_set_payload(response, buffer, len); 

		}else
			LOG_INFO("Error: Response message not formed\n");
	}else{
		coap_set_status_code(response, NOT_ACCEPTABLE_4_06);
	   	const char *msg = "Supported content-types:application/json";
	    	coap_set_payload(response, msg, strlen(msg));
	}
	
	//coap_set_header_content_format(response, TEXT_PLAIN);
	//coap_set_payload(response, buffer, snprintf((char *)buffer, preferred_size, "TEMPERATURE: %i\n", temp_value));
	

}


// CHANGE THE MIN AND MAX VALUE OF TEMPERATURE WHEN LIGHT IS SWITCHED ON/OFF.

static void res_post_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){

	
	if(request == NULL){

		LOG_INFO("[TEMP]: Empty request\n");
		return;
	}

	size_t pay_len = 0;
	const char *variation_mode = NULL;
	int value = 0;
	bool good_req = false;

	const uint8_t **message;
	message = malloc(request->payload_len);

	if(message == NULL){
		LOG_INFO("[TEMP]: Empty payload\n");
		return;
	}

	pay_len = coap_get_payload(request, message);
	LOG_INFO("Message received: %s\n", (char *)*message);

	
	if(pay_len > 0){
		
		//Splitting the payload
		char *split;

		//Take the variable
		split = strtok((char*)*message, "=");
		if((split && strcmp(split, "increase") == 0) || (split && strcmp(split, "decrease") == 0)){
			variation_mode = split;
		}
		/*
		//Take the value
		split = strtok(NULL, "=");
		if(split && isdigit(split))
			value = atoi(split);

		//Payload lenght wrong!
		if(pay_len != strlen(variation_mode) + strlen(split) + 1){
			variation_mode = NULL;
			value = 0;
		}
		*/
		value = 5;

	}

	LOG_INFO("Variation Type: %s, Value: %s\n", variation_mode, value);


	if(variation_mode != NULL && value != 0){	
		if(strcmp(variation_mode, "increase") == 0) {
			min_temp_value += value;
			max_temp_value += value;
			good_req = true;
			
			LOG_DBG("Max and Min temperature increased\n");	
	
		}else if(strcmp(variation_mode, "decrease") == 0) {
			min_temp_value -= value;
			max_temp_value -= value;
			good_req = true;
	
			LOG_DBG("Max and Min temperature decreased\n");
		}
		LOG_INFO("New Max: %s, New Min: %s\n", max_temp_value, min_temp_value);

	}

	if(!good_req)
		coap_set_status_code(response, BAD_REQUEST_4_00);
	


}
