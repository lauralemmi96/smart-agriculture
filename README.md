# SmartAgriculture Project for IoT course
CoAP application for vegetable garden management. 


# How to run
Place the smart-agriculture directory in contiki/example/
1) Import the maven project. It must be placed in contiki-ng/examples/smart-agriculture


    contiki-ng/examples/smart-agriculture
    |
    |
      -- device
      -- rpl-border-router
      -- it.unipi.iot
          |
          |
            -- src/main/java/it/unipi/iot
            -- target
      -- simulation.csc
      -- app.sh
      -- br.sh
            

3) Deploy the network (simulation.csc) in Cooja simulator
4) Run the border router (./br)
5) Run the application (./app)
