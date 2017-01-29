#### TODO: I use canvas and 2d graph context for rendering. Currently I re-render each displayable in each frame. However, a better approach is to set a dirty bit for displayable and only re-render a displayable if it's dirty (has been changed during the frame).

### Breakout Game 打砖块, for Duke CompSci 308.
#### -- by Keping Wang

Starting from Jan 10, finished at Jan 21. Estimated 40 hours.

The game starts from GameApp.main().

Required files are specified in classpath, including text level configuration and images.

#### Keys and Cheat Keys:

Shoot (ball or bullet): SPACE, ENTER, or LEFT\_CLICK

Move: <-, ->, A, D, or MOUSE\_MOVEMENT

Pause: P

Change balls speed: -, +

Go to next level: N

Reset ball: R

Add life: L

Split ball: B

Make bat sticky 15s: S

Make bat GUNNER 15s: G

Change ball size (small, mid, big): Z, X, C

Change bat size (short, mid, long): 1, 2, 3

#### Other notes:

Only three of the followings are implemented to be power ups: G, S, L. Just try playing with cheat keys (without directly jumping to the next level).

There is an easter egg about the gun. Use G to equip a gun and SHOOT :)

This game has three levels. Have fun playing~
