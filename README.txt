AIRobot: From FTC Chassis to Self-Driving Robot

My objective in this project was to repurpose an FTC chassis 
used in competitions to experiment with and build a self-driving 
robot. Here are the robotic components I had to work with:

- Rev Robotics Expansion Hub
- 4 GoBilda mecanum wheels
- GoBilda Chassis
- 2 Motorola cell phones acting as the Robotics Controller and Driver stations
- Logitech Joystick

To develop the self-driving capability, I replaced the Android 
Robotics Controller with a Raspberry Pi and used a MacBook as 
the Driver station, networking over Wi-Fi. Porting the FTC code 
to the Raspberry Pi presented a significant challenge, especially 
when it came to interfacing the Raspberry Pi with the Rev Robotics 
Expansion Hub due to the lack of a data sheet for the hub. To 
overcome this hurdle, I relied on the [OpenRC-Turbo](https://github.com/OpenFTC/OpenRC-Turbo) 
repository on GitHub to reverse engineer the USB command parameters 
of the Rev Robotics Expansion Hub for motor control.

I enabled communication between the Driver station (MacBook) 
and the Robotics controller (Raspberry Pi) by developing a web 
server on the Raspberry Pi. This allowed the Driver station and 
the self-driving engine (Python) to communicate over HTTP.

Model Training Process

To train the self-driving car model, I implemented a custom 
training mode on the robot. The robot collected essential data 
in this mode, including the steering angle, corresponding motor 
power, and an image of the track on which it was moving. With 
this data, I trained the model using the [Dave-2 End to End Learning 
Model](https://developer.nvidia.com/blog/deep-learning-self-driving-cars/), 
a deep learning approach designed for autonomous driving. Following 
training, the model underwent rigorous testing by running the robot 
on a variety of tracks, including both familiar paths and entirely 
new environments.

I hope this project serves as a resource for those seeking to explore 
similar endeavors, showcasing the potential of repurposing existing 
robotics components for exciting projects.

Here you can see a demonstration of the self-driving robot:
https://youtu.be/-P5vUNEftOE

Credits and Resources

AIRobot would not have been possible without the contributions 
and open-source work from the following repositories:

- OpenRC-Turbo](https://github.com/OpenFTC/OpenRC-Turbo): 
  This repository provided the foundation for understanding 
  and porting the FTC code to the Raspberry Pi platform. The 
  work done here greatly aided in adapting the FTC chassis for 
  use in this project.
- [DeepPiCar](https://github.com/dctian/DeepPiCar): This 
  project was instrumental in guiding the integration of a camera 
  and the development of the self-driving model on the Raspberry 
  Pi. The insights gained from DeepPiCar helped in overcoming 
  the challenges of implementing autonomous navigation.

I extend my gratitude to the developers and contributors of these 
projects for their efforts in advancing the field of robotics and 
making these resources available to the community.

---

Important Note:

This project is meant for fun and learning. The code or components 
recommended in this project must not and cannot be used in FTC.
