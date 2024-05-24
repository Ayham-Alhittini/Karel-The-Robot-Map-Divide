import stanford.karel.*;

public class Solution extends SuperKarel {
    final int INF = Integer.MAX_VALUE;
    int width = 1, height = 1, stepCounter = 0;
    enum Direction {turnLeft, turnRight, DontTurn}

    public void run() {
        reset();
        setBeepersInBag(1000);
        setDimension();
        getWorldTypeAndDivideChampers();
        backToStartPoint();
        System.out.println("Total number of steps = " + stepCounter);
    }
    private void getWorldTypeAndDivideChampers() {
        if (width < 3 || height < 3)
            specialWorld();
        else if (width % 2 == 1 || height % 2 == 1)
            withOddWorld();
        else
            evenWorld();
    }
    private void withOddWorld() {
        moveFor((int) Math.ceil(width / 2.0) - 1 , Direction.turnLeft);
        oddWorldDivide(width, height);
        moveLShape(INF, (int) Math.ceil(height / 2.0) - 1, Direction.turnLeft, Direction.turnLeft);
        oddWorldDivide(height, width);
    }
    private void evenWorld() {
        if (couldMinimizeDoubleLine()) {
            int reducedDoubleLineLength = Math.abs(height - width) + 2;
            moveFor(width / 2 - (width > height ? 1 : 0), Direction.turnLeft);

            if (width <= height) {
                divideBothEvenWithReduceDoubleLineVertically(reducedDoubleLineLength);
                moveLShape(INF, width / 2 - 1, Direction.turnRight, Direction.turnRight);
                divideBothEvenWithReduceDoubleLineVertically(reducedDoubleLineLength);
            }
            else {
                divideBothEvenWithReduceDoubleLineHorizontally(reducedDoubleLineLength);
                moveLShape(INF, width / 2, Direction.turnRight, Direction.turnRight);
                divideBothEvenWithReduceDoubleLineHorizontally(reducedDoubleLineLength);
            }
        }
        else {
            moveFor((int) Math.ceil(width / 2.0) - 1 , Direction.turnLeft);
            divideEvenWorld(height, width);
            moveLShape(INF, (int) Math.ceil(height / 2.0) - 1, Direction.turnLeft, Direction.turnLeft);
            divideEvenWorld(width, height);
        }
    }
    private void specialWorld() {
        int maxNumberOfChamber = 4, maxDimension = Math.max(height, width), minDimension = Math.min(height, width);
        while ((maxDimension - (maxNumberOfChamber - 1)) * minDimension / maxNumberOfChamber == 0 ) maxNumberOfChamber--;
        int chamberArea = (maxDimension - maxNumberOfChamber + 1) * minDimension / maxNumberOfChamber;

        if (minDimension == 1) {
            if (height > width) turnLeft();
            moveFor(chamberArea, Direction.DontTurn);

            if (maxNumberOfChamber > 1) moveFor(chamberArea + (maxNumberOfChamber > 2 ? 1 : 0), false, true, Direction.DontTurn);
            if (maxNumberOfChamber > 2) moveFor(chamberArea + (maxNumberOfChamber > 3 ? 1 : 0), false, true, Direction.DontTurn);
            if (maxNumberOfChamber > 3) moveFor(chamberArea + (maxNumberOfChamber > 4 ? 1 : 0), false, true, Direction.DontTurn);

            moveFor((maxDimension - (maxNumberOfChamber - 1)) % maxNumberOfChamber, true, false, Direction.turnLeft);
        }
        else {
            if (height > width) moveFor(1, Direction.turnLeft);

            if (maxDimension <= 4) {
                turnLeft();
                cornerMove(1);
                if (frontIsClear()) {
                    turnRight();
                    moveLShape(1, 1, Direction.turnLeft, Direction.turnLeft);
                    if (rightIsClear()) cornerMove(1);
                    else putBeeperIfNotPresent();
                }
                return;
            }

            if (maxDimension % 2 == 0) {
                turnLeft();
                moveFor(1, true, true, Direction.DontTurn); turnAround();
                moveLShape(1, 1, Direction.turnLeft, Direction.DontTurn);
            }
            moveFor(chamberArea / 2, Direction.turnLeft);
            cornerMove(chamberArea);
            moveFor(chamberArea / 2 + 1, false, false, Direction.turnRight);
            moveFor(1, true, true, Direction.turnLeft);
            moveFor(chamberArea / 2 + 1, false, false, Direction.turnLeft);
            cornerMove(chamberArea);
        }
    }
    private void oddWorldDivide(int currentDimension, int otherDimension) {
        if (currentDimension % 2 == 1)
            moveFor(otherDimension, true, true, Direction.turnLeft);
        else
            drawCurveLine(otherDimension, 1);
    }
    private void divideEvenWorld(int dimension1, int dimension2) {
        if (dimension1 < dimension2) {
            drawDoubleLine(dimension1, Direction.turnRight); turnLeft();
        }
        else drawCurveLine(dimension1, 2);
    }
    private void cornerMove(int chamberArea) {
        if (chamberArea % 2 == 1) {
            moveFor(1, false, true, Direction.turnRight);
            moveFor(1, true, false, Direction.DontTurn);
        } else moveFor(1, true, true, Direction.turnRight);
    }
    private boolean couldMinimizeDoubleLine() { return Math.abs(height - width) + 2 < Math.min(height, width); }
    private void divideBothEvenWithReduceDoubleLineHorizontally(int reducedDoubleLineLength) {
        moveFor(height / 2, true, true, Direction.turnLeft);
        moveSafeAndCount();
        drawDoubleLine(reducedDoubleLineLength / 2 - 1, Direction.turnLeft);
        if (reducedDoubleLineLength % 4 == 0)
            moveLShape(1, 1, Direction.turnRight, Direction.turnLeft);
        else
            moveSafeAndCount();
        moveFor(INF, true, true, Direction.turnRight);
    }
    private void divideBothEvenWithReduceDoubleLineVertically(int reducedDoubleLineLength) {
        moveFor(height / 2 - reducedDoubleLineLength / 2 - 1, true, true, Direction.DontTurn);
        if (reducedDoubleLineLength % 4 == 0) {
            turnLeft();
            moveFor(1, Direction.turnRight);
        }
        moveSafeAndCount();
        drawDoubleLine(reducedDoubleLineLength / 2, reducedDoubleLineLength % 4 == 0 ? Direction.turnRight : Direction.turnLeft);
        turnLeft();
        moveFor(INF, true, false, Direction.turnRight);
    }
    private void drawCurveLine(int distance, int spaceBetween) {
        int curveLineLength = (distance - spaceBetween) / 2;
        moveFor(curveLineLength / 2 - (curveLineLength % 2 == 0 ? 1 : 0), true, true, Direction.turnRight);
        moveFor(1, curveLineLength % 2 == 1, false, Direction.turnLeft);
        moveFor(curveLineLength + spaceBetween, true, false, Direction.turnLeft);
        moveFor(1, curveLineLength % 2 == 1, false, Direction.turnRight);
        moveFor(curveLineLength / 2, true, false, Direction.turnLeft);
    }
    private void drawDoubleLine(int distance, Direction direction) {
        if (distance == 0)return;
        changeDirection(direction);
        moveFor(1, true, true, direction == Direction.turnRight ? Direction.turnLeft : Direction.turnRight);
        int startWith = direction == Direction.turnRight ? 0 : 1;
        for (int i = 0; i < distance - 1; i++) {
            if (i % 2 == startWith) {
                moveFor(1, true, false, Direction.turnLeft);
                moveFor(1, true, false, Direction.turnRight);
            } else {
                moveFor(1, true, false, Direction.turnRight);
                moveFor(1, true, false, Direction.turnLeft);
            }
        }
    }
    private void moveFor(int distance, Direction turn) {moveFor(distance, false, false, turn);}
    private void moveFor(int distance, boolean withBeepers, boolean initialBeeper, Direction turn) {
        if (initialBeeper) putBeeperIfNotPresent();
        while (distance-- > 0 && moveSafeAndCount()) if (withBeepers) putBeeperIfNotPresent();
        changeDirection(turn);
    }
    private void moveLShape(int distance1, int distance2, Direction turn1, Direction turn2) {
        moveFor(distance1, turn1);
        moveFor(distance2, turn2);
    }
    private boolean moveSafeAndCount() {
        if (frontIsClear()) {
            move();
            stepCounter++;
            return true;
        }
        return false;
    }
    private void setDimension() {
        while (moveSafeAndCount())
            width++;
        turnLeft();
        while (moveSafeAndCount())
            height++;
        turnLeft();
    }
    private void reset() {
        stepCounter = 0;
        width = height = 1;
    }
    private void putBeeperIfNotPresent() {if (noBeepersPresent()) putBeeper();}
    private void changeDirection(Direction direction) {
        if (direction == Direction.turnRight) turnRight();
        else if (direction == Direction.turnLeft) turnLeft();
    }
    private void backToStartPoint() {
        while (notFacingSouth()) turnLeft(); moveFor(INF, Direction.DontTurn);
        while (notFacingWest()) turnLeft(); moveFor(INF, Direction.DontTurn);
        while (notFacingEast()) turnLeft();
    }
}