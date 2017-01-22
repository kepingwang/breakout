## Design Choices:

The main game loop is implemented with javafx AnimationTimer. Within each iteration of the loop, the following actions are taking:

1. Handle user input accordingly set some states of sprites.

2. Collision resolution.

3. Update sprite positions (states) to frame end time.

4. Render the images on the canvas.

### Collision Detection and Resolution:

Collision handling uses the method of event driven simulation, which takes nlogn time within each frame. 

Whenever the trajectory of a sprite changes (including initilization), predict all future collsions involving this sprite and store them in a PriorityQueue, where the top element is predicted to happen in the nearest future.

Poll collisions from the PriorityQueue one by one. A collision is invalid if any involved sprite changed trajectory after the time prediction was made. If a collision is valid, update all sprite positions to the predicted collision happening time, and resolve the collision by changing velocity and taking special effects.

Collision is predicted by physical laws (in my system I assume no acceleration). Collision happens between two sprites. However, two sprites can have different kinds of collisions, for example, ball collision with the edge of the brick and collision with the corner of the brick are different.

So I design the class Collidable. Each sprite contains collidables and each collidable belongs to a sprite. Collidable object remembers their relative position with respect to the sprite and gets their position and velocity from the sprite. Therefore a collision actually happens between two collidables. There are three types of collidables, Circle, VLine (vertical line), and HLine (horizontal line). Collision prediction and resolution between collidables still have to take into consideration the sprites that the collidables belong to.

### Adding New Features:

I'm quite confident with my design that I expect there to be no problem adding some basic features like:

1. Different ball attack effects.

    a. Going through bricks: just change collision resolution between balls and bricks.
	
    b. Explosive balls: add special effects of ball-brick collision resolution to set neighboring bricks to explode.
	
	
2. Use images for sprites: just change the rendering method.
