# AIRobot: From FTC Chassis to Self-Driving Vehicle

Inspired by a previously used FTC chassis and a growing interest in autonomous 
vehicles, AIRobot is a project born out of the desire to repurpose existing 
resources.

Initially, I explored potential project ideas on Reddit, hoping to find 
inspiration for my unused FTC chassis. However, the FTC code being partially 
close-sourced and designed exclusively for Android phones became apparent, 
hindering my ability to explore alternative applications. Undeterred by these 
constraints, I decided to embark on a new path.

To unlock the full potential of the chassis, I undertook the task of porting 
the FTC code to the Raspberry Pi platform. This pivotal step expanded the 
project’s horizons, enabling the integration of a camera and the development 
of a self-driving model. Through rigorous training and testing, the model 
learned to navigate its environment autonomously.

## Model Training Process

To train the self-driving car model, I implemented a custom training mode on 
the robot. This mode captured essential data, including the steering angle, 
corresponding motor power, and an image of the track on which the robot was 
moving. With this data, I trained the model using the [Dave-2 End to End Learning 
Model](https://developer.nvidia.com/blog/deep-learning-self-driving-cars/), 
a deep learning approach designed for autonomous driving. After training, the 
model was rigorously tested, running the robot on a variety of tracks, including 
both familiar paths and completely new environments.

While challenges arose during the development process, including the need to 
overcome the power limitations of Android phones and optimize model performance 
on the Raspberry Pi architecture, the end result is a testament to the power of 
perseverance and innovation. AIRobot stands as a proof of concept, demonstrating 
the feasibility of transforming a seemingly obsolete component into a cutting-edge 
autonomous vehicle.

This project serves as a valuable resource for those seeking to explore similar 
endeavors, showcasing the potential of repurposing existing robotics components 
to create groundbreaking projects.

## Credits and Resources

AIRobot would not have been possible without the contributions and open-source 
work from the following repositories:

- **[OpenRC-Turbo](https://github.com/OpenFTC/OpenRC-Turbo)**: This repository 
provided the foundation for understanding and porting the FTC code to the Raspberry 
Pi platform. The work done here greatly aided in adapting the FTC chassis for use 
in this project.

- **[DeepPiCar](https://github.com/dctian/DeepPiCar)**: This project was instrumental 
in guiding the integration of a camera and the development of the self-driving model 
on the Raspberry Pi. The insights gained from DeepPiCar helped in overcoming the 
challenges of implementing autonomous navigation.

I extend my gratitude to the developers and contributors of these projects for 
their efforts in advancing the field of robotics and making these resources available 
to the community.
