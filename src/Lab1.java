import TSim.*;

import java.util.concurrent.Semaphore;

import static java.lang.Thread.sleep;

public class Lab1 {

    public Lab1(int speed1, int speed2) {
        TSimInterface tsi = TSimInterface.getInstance();

        try {
            tsi.setSpeed(1, speed1);
            tsi.setSpeed(2, speed2);

        } catch (CommandException e) {
            e.printStackTrace();    // or only e.getMessage() for the error
            System.exit(1);
        }

        Semaphore a = new Semaphore(0);  // North West of cross-section
        Semaphore cross = new Semaphore(1);
        Semaphore b = new Semaphore(1);
        Semaphore c = new Semaphore(1);
        Semaphore d = new Semaphore(1);
        Semaphore e = new Semaphore(0);
        Semaphore f = new Semaphore(1);
        //Semaphore g = new Semaphore(1);      Near South Station?


        class Train implements Runnable {


            int trainId;
            int speed;
            boolean direction;

            public Train(int trainId, int speed, boolean dir) {
                this.trainId = trainId;
                this.speed = speed;
                this.direction = dir;
            }

            @Override
            public void run() {
                try {
                    while (true) {
                        SensorEvent se = tsi.getSensor(trainId);


                        // A-Line  North West of Cross section
                        if (se.getXpos() == 6 && se.getYpos() == 6 && se.getStatus() == SensorEvent.ACTIVE) {
                            // Move south
                            System.out.println(direction);

                            if (!direction) {
                                System.out.println("Move South");
                                tsi.setSpeed(trainId, 0);
                                cross.acquire();
                                tsi.setSpeed(trainId, speed);
                            } else { // Move North
                                System.out.println("Move North");
                                cross.release();
                            }
                        }

                        // A-line east of cross section
                        if (se.getXpos() == 15 && se.getYpos() == 7 && se.getStatus() == SensorEvent.ACTIVE) {
                            System.out.println(direction);

                            //Move South
                            if (!direction) {
                                System.out.println("Move South");
                                tsi.setSpeed(trainId, 0);
                                b.acquire();
                                a.release();
                                System.out.println("a value: " + a.availablePermits());
                                tsi.setSwitch(17, 7, TSimInterface.SWITCH_RIGHT);
                                tsi.setSpeed(trainId, speed);

                            } else {
                                System.out.println("Move North");
                                b.release();

                            }

                        }


                        // B-line east of cross section
                        if (se.getXpos() == 10 && se.getYpos() == 7 && se.getStatus() == SensorEvent.ACTIVE) {
                            //Move South
                            if (!direction) {
                                System.out.println("Move South");
                                cross.release();
                                // b
                            } else { // Move North
                                System.out.println("Move North");
                                cross.acquire();
                            }
                        }

                        // C-line
                        if (se.getXpos() == 19 && se.getYpos() == 8 && se.getStatus() == SensorEvent.ACTIVE) {
                            // Move South
                            if (!direction) {
                                System.out.println("Move South");
                                tsi.setSpeed(trainId, 0);
                                //Switch to D
                                if (d.tryAcquire()) {
                                    System.out.println("D");
                                    tsi.setSwitch(15, 9, TSimInterface.SWITCH_RIGHT);
                                } else {
                                    // Swicth to E
                                    System.out.println("E");
                                    e.acquire();
                                    tsi.setSwitch(15, 9, TSimInterface.SWITCH_LEFT);

                                }
                                tsi.setSpeed(trainId, speed);
                            } else {
                                System.out.println("Move North");
                                tsi.setSpeed(trainId, 0);
                                if (a.tryAcquire()) {
                                    System.out.println("A");
                                    c.release();
                                    tsi.setSwitch(17, 7, TSimInterface.SWITCH_RIGHT);
                                } else {
                                    System.out.println("B");
                                    c.release();
                                    tsi.setSwitch(17, 7, TSimInterface.SWITCH_LEFT);
                                }
                                tsi.setSpeed(trainId, speed);

                            }


                        }


                        // D-line
                        if (se.getXpos() == 6 && se.getYpos() == 10 && se.getStatus() == SensorEvent.ACTIVE) {
                            System.out.println(direction);
                            // Move south
                            if (!direction) {
                                System.out.println("Move South");
                                tsi.setSpeed(trainId, 0);
                                f.acquire();
                                d.release();
                                tsi.setSwitch(4, 9, TSimInterface.SWITCH_RIGHT);
                                tsi.setSpeed(trainId, speed);

                            } else {
                                System.out.println("Move North");
                                f.release();
                            }


                        }

                        if (se.getXpos() == 12 && se.getYpos() == 9 && se.getStatus() == SensorEvent.ACTIVE && direction) {


                            //Move North

                            System.out.println("Move North");
                            tsi.setSpeed(trainId, 0);
                            c.acquire();
                            d.release();
                            tsi.setSwitch(15, 9, TSimInterface.SWITCH_RIGHT);
                            tsi.setSpeed(trainId, speed);


                        }


                        // E-Line


                        // F-Line North
                        //if()


                        // F-Line South
                        if (se.getXpos() == 1 && se.getYpos() == 10 && se.getStatus() == SensorEvent.ACTIVE) {
                            System.out.println(direction);
                            //Move South
                            if (!direction) {
                                System.out.println("Move South");
                                tsi.setSpeed(trainId, 0);
                                f.release();

                                tsi.setSwitch(3, 11, TSimInterface.SWITCH_RIGHT);
                                tsi.setSpeed(trainId, speed);

                            } else {  // Move North

                                tsi.setSpeed(trainId, 0);
                                if (d.tryAcquire()) {

                                    System.out.println("Switch to D");

                                    //d.acquire();
                                    System.out.println("D" + d.availablePermits());

                                    f.release();
                                    System.out.println("F" + f.availablePermits());

                                    tsi.setSwitch(4, 9, TSimInterface.SWITCH_LEFT);
                                } else {
                                    System.out.println("Switch to E");
                                    e.acquire();
                                    f.release();
                                    tsi.setSwitch(4, 9, TSimInterface.SWITCH_RIGHT);

                                }
                                tsi.setSpeed(trainId, speed);


                            }
                        }

                        // South Station also the check correct direction
                        if (se.getXpos() == 13 && (se.getYpos() == 11 || se.getYpos() == 13) && se.getStatus() == SensorEvent.ACTIVE && !direction) {
                            System.out.println("Reached");
                            tsi.setSpeed(trainId, 0);
                            sleep(2000);
                            speed = -speed;
                            direction = !direction;
                            //System.out.println(speed);
                            tsi.setSpeed(trainId, speed);
                            System.out.println(direction);
                        }

                        //North Station
                        if (se.getXpos() == 16 && (se.getYpos() == 5 || se.getYpos() == 3) && se.getStatus() == SensorEvent.ACTIVE && direction) {
                            tsi.setSpeed(trainId, 0);
                            sleep(2000);
                            speed = -speed;
                            direction = !direction;
                            tsi.setSpeed(trainId, speed);
                        }


                    }


                } catch (Exception e) {
                    e.printStackTrace();

                }
            }

        }

        Thread train1 = new Thread(new Train(1, speed1, false)); // move south
        train1.start();

        Thread train2 = new Thread(new Train(2, speed2, true)); // move north
        train2.start();
    }
}
