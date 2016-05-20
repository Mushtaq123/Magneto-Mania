package com.sdsmdg.kd.gameplay.objects;

import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.sdsmdg.kd.helpers.InputHandler;
import com.sdsmdg.kd.magnetomania.Main;

/**
 * @author Haresh Khanna
 */
public class Laser extends GameObject {
    private RandomXS128 random;
    public Vector2 screenCenter;

    /**
     * Denotes the number of quarter turns.
     */
    public int numberOfTurns;

    /**
     * Denotes the time taken for on quarter turn.
     */
    public int turnTime;

    /**
     * There is a need for two point velocities because each couple of diametrically
     * opposite points have different velocities as they have different distances to
     * cover in the same amount of time, the two different distances being: Height and
     * Width.
     */
    public int[] pointVelocity;

    /**
     * This array of Vector2 type will store the end points of the line segment
     * joining the center of the Magnus to the other end of the laser line. Thus
     * the Laser obstacle basically consists of four line segments whose one end
     * point is common and is at the center of the circle and other end point is
     * defined by the following points.
     */
    public Vector2[] endPoints;

    public Laser() {
        this.random = new RandomXS128();

        /**
         * For instantiating a Vector2 type array.
         */
        this.endPoints = new Vector2[5];
        for (int i=0;i<5;i++) {
            this.endPoints[i] = new Vector2(-10,-10);
        }
        this.pointVelocity = new int[2];
        this.numberOfTurns = 0;
        this.turnTime = 0;
        this.screenCenter = new Vector2((Main.screen.x)/2,(Main.screen.y)/2);

        //the weapon is Initially deactivated.
        this.active = false;

        //sets the end points out of the screen initially
        pointVelocity[0] = pointVelocity[1] = 0;
    }

    public void setMagnusVelocityComponents (Magnus magnus) {
        magnus.velocity = random.nextInt(15) + 15;
        magnus.calcVelocityComponent(screenCenter);
    }

    public void moveMagnus(Magnus magnus) {
        magnus.add(velocityComponent);
    }

    public void init (Magnus magnus) {
        activate();
        setMagnusVelocityComponents(magnus);
        moveMagnus(magnus);
        numberOfTurns = 4 + random.nextInt(12);
        turnTime = 20 + random.nextInt(50);
        pointVelocity[0] = (int) (Main.screen.x/turnTime); // lesser speed
        pointVelocity[1] = (int) (Main.screen.y/turnTime); // greater speed

        // center point
        endPoints[0] = screenCenter;

        //left up
        endPoints[1].x = 0;
        endPoints[1].y = 0;

        //left down
        endPoints[2].x = 0;
        endPoints[2].y = Main.screen.y;

        //right down
        endPoints[3].x = Main.screen.x;
        endPoints[3].y = Main.screen.y;

        //right up
        endPoints[4].x = Main.screen.x;
        endPoints[4].y = 0;
    }

    public void reset (Magnus magnus) {
        deactivate();
        magnus.calcVelocityComponent(new Vector2(InputHandler.touch.x,InputHandler.touch.y));
        moveMagnus(magnus);
        numberOfTurns = 0;
        turnTime = 0;
        pointVelocity[0] = pointVelocity[1] = 0;
        endPoints[0].x = endPoints[1].x = endPoints[2].x = endPoints[3].x = endPoints[4].x = -10;
        endPoints[0].y = endPoints[1].y = endPoints[2].y = endPoints[3].y = endPoints[4].y = -10;
    }

    public void rotateLaser () {
        if (endPoints[1].y < Main.screen.y || endPoints[2].x < Main.screen.x
                || endPoints[3].y > 0 || endPoints[4].x>0) {
            endPoints[2].x = endPoints[2].x + pointVelocity[0];
            endPoints[4].x = endPoints[4].x - pointVelocity[0];
            endPoints[1].y = endPoints[1].y + pointVelocity[1];
            endPoints[3].y = endPoints[3].y - pointVelocity[1];
        }
        else {
            numberOfTurns --;
            Vector2 temp = endPoints[1];
            endPoints[1] = endPoints[4];
            endPoints[4] = endPoints[3];
            endPoints[3] = endPoints[2];
            endPoints[2] = temp;
        }
    }
}
