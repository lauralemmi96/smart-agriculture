# SmartAgriculture Project for IoT course
CoAP application for vegetable garden management. 


# How to run
Place the smart-agriculture directory in contiki/example/
1) Import the maven project. It must be placed in contiki-ng/examples/smart-agriculture


   contiki-ng/examples/smart-agriculture <br />
____| <br />
____| <br />
________-- device <br />
________-- rpl-border-router <br />
________-- it.unipi.iot <br />
_________| <br />
_________| <br />
__________-- src/main/java/it/unipi/iot <br />
__________-- target <br />
________-- simulation.csc <br />
________-- app.sh <br />
________-- br.sh <br />
            

3) Deploy the network (simulation.csc) in Cooja simulator
4) Run the border router (./br)
5) Run the application (./app)
