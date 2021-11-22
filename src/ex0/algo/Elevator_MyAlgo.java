package ex0.algo;

import ex0.Building;
import ex0.CallForElevator;
import ex0.Elevator;
import java.util.LinkedList;

//207689621
public class Elevator_MyAlgo implements ElevatorAlgo
{

    class fixedElevator
    {
        public LinkedList<CallForElevator> listOfCalls;
        public fixedElevator()
        {
            listOfCalls = new LinkedList<CallForElevator>();
        }
    }
    public static final int UP=1, DOWN=-1, LEVEL=0;
    private Building building;
    private fixedElevator[] list; //list of all the elevators. in each cell we have the call list.
    public Elevator_MyAlgo(Building b)
    {
        building=b;
        list = new fixedElevator[b.numberOfElevetors()];//array of elevators and in every cell we have call list
        for(int i=0; i<list.length; i++)
        {
            list[i] = new fixedElevator();
        }
    }
    @Override
    public Building getBuilding()
    {
        return building;
    } // return the building
    @Override
    public String algoName()
    {
        return "Ex0_OOP_My Elevator Algo";
    }


    @Override
    public int allocateAnElevator(CallForElevator call) //find the best elevator to assign for a specific call.
    {
        if (building.numberOfElevetors() == 1) //if theres only 1 elevator in the building.
        {
            list[0].listOfCalls.add(call);
            return 0;
        }
        int answer = bestTime(call); //number of best elevator
        list[answer].listOfCalls.add(call); //add the call to the call list of given elevator
        return answer;
    }

    private int bestTime(CallForElevator call)//find the elevator with best time for the call
    {
        int answer=0;
        double time1 = checkTotalTime(building.getElevetor(0), list[0].listOfCalls, call);
        double time2;
        for(int i=0; i<building.numberOfElevetors(); i++) //go over all the elevator and check best option
        {
            time2 = checkTotalTime(building.getElevetor(i), list[i].listOfCalls, call);
            if(time2 < time1)
            {
                time1 = time2;
                answer = i;
            }
        }
        return answer;
    }

    private double checkTotalTime(Elevator e, LinkedList<CallForElevator> l, CallForElevator call)
    // check the elevator time of all its calls including the new one
    {
        LinkedList<CallForElevator> newList = new LinkedList<CallForElevator>();
        for(int i=0; i<l.size(); i++) //simulate the new list as our original list with the new call.
            newList.add(l.get(i));
        newList.add(call);

        double time = 0.0;
        int nextFloor; //next floor to reach
        int current = e.getPos(); //current location
        int amountOfFloorsUP = checkAmountOfFloors(newList, DOWN); //how many floors we actually have
        int amountOfFloorsDOWN = checkAmountOfFloors(newList, UP); //how many floors we actually have
        int direction = checkDirection(newList, current);

        if(direction == UP)
        {
            nextFloor = findLowFloorUP(newList);
            if(nextFloor != building.maxFloor())
            {
                time = time + checkTime(e, current, nextFloor);
                current = nextFloor;
                amountOfFloorsUP--; //we have already been to the first, so decrease by 1
            }
            for (int i = 0; i < amountOfFloorsUP; i++) //go over all the floor we need to reach and check total amount of time
            {
                nextFloor = checkNextFloor(newList, direction, current);
                time = time + checkTime(e, current, nextFloor);
                current = nextFloor;
            }
            direction = DOWN;
            nextFloor = findHighFloorDOWN(newList);
            if(nextFloor != building.minFloor())
            {
                time = time + checkTime(e, current, nextFloor);
                current = nextFloor;
                amountOfFloorsDOWN--; //we have already been to the first, so decrease by 1
            }
            for (int i = 0; i < amountOfFloorsDOWN; i++) //go over all the floor we need to reach and check total amount of time
            {
                nextFloor = checkNextFloor(newList, direction, current);
                time = time + checkTime(e, current, nextFloor);
                current = nextFloor;
            }
        }
        else
        {
            nextFloor = findHighFloorDOWN(newList);
            if(nextFloor != building.minFloor())
            {
                time = time + checkTime(e, current, nextFloor);
                current = nextFloor;
                amountOfFloorsDOWN--; //we have already been to the first, so decrease by 1
            }
            for (int i = 0; i < amountOfFloorsDOWN; i++) //go over all the floor we need to reach and check total amount of time
            {
                nextFloor = checkNextFloor(newList, direction, current);
                time = time + checkTime(e, current, nextFloor);
                current = nextFloor;
            }
            direction = UP;
            nextFloor = findLowFloorUP(newList);
            if(nextFloor != building.maxFloor())
            {
                time = time + checkTime(e, current, nextFloor);
                current = nextFloor;
                amountOfFloorsUP--; //we have already been to the first, so decrease by 1
            }
            for (int i = 0; i < amountOfFloorsUP; i++) //go over all the floor we need to reach and check total amount of time
            {
                nextFloor = checkNextFloor(newList, direction, current);
                time = time + checkTime(e, current, nextFloor);
                current = nextFloor;
            }
        }
        return time;
    }

    private int checkDirection(LinkedList<CallForElevator> calls, int current)
    {//check the best direction we need to start with based on the distance of the elevator and the lowest src up call
        //and the highest src down call.
        boolean callsUP = false;
        boolean callsDOWN = false;

        for(int i=0; i<calls.size(); i++) //check if we have only up calls or only down calls.
        {
            if(calls.get(i).getType() == UP)
                callsUP = true;
            if(calls.get(i).getType() == DOWN)
                callsDOWN = true;
        }

        if(callsUP == false)
            return DOWN;
        if (callsDOWN == false)
            return  UP;

        int firstFloorUP = findLowFloorUP(calls);
        int firstFloorDOWN = findHighFloorDOWN(calls);
        if(dist(current, firstFloorDOWN) < dist(current, firstFloorUP))
            return DOWN;
        else
            return UP;
    }

    private int findLowFloorUP(LinkedList<CallForElevator> calls) //find the lowest up type floor
    {
        int firstFloorUP = building.maxFloor();
        for(int i=0; i<calls.size(); i++)
        {
            if (calls.get(i).getType() == UP) //the call is up type
                if (calls.get(i).getSrc() < firstFloorUP)
                    firstFloorUP = calls.get(i).getSrc();
        }
        return firstFloorUP;
    }

    private  int findHighFloorDOWN(LinkedList<CallForElevator> calls)//find the highest down type floor
    {
        int firstFloorDOWN = building.minFloor();
        for(int i=0; i<calls.size(); i++)
        {
            if(calls.get(i).getType() == DOWN)
                if(calls.get(i).getSrc() > firstFloorDOWN)
                    firstFloorDOWN = calls.get(i).getSrc();
        }
        return firstFloorDOWN;
    }

    private int checkAmountOfFloors(LinkedList<CallForElevator> calls, int direction) //check how many floors we need to reach
    {
        int answer=0;

        for(int i=0; i<calls.size(); i++)
        {
            if(calls.get(i).getType() == direction)
            {
                answer = answer + 2;
                for (int j=0; j<calls.size(); j++)
                {
                    if(i!=j && calls.get(j).getType() == direction && calls.get(j).getSrc() == calls.get(i).getSrc())
                        answer--;
                    if(i!=j && calls.get(j).getType() == direction && calls.get(j).getDest() == calls.get(i).getDest())
                        answer--;
                }
            }
        }
        return answer;
    }

    private int checkNextFloor(LinkedList<CallForElevator> calls, int direction, int current)
    //check the closest floor we need to reach based on our current location and direction
    {
        int answer;
        if(direction == UP)
        {
            answer = building.maxFloor();
            for(int i=0; i<calls.size(); i++)
                if(calls.get(i).getType() == UP)
                    if(calls.get(i).getSrc() < answer && calls.get(i).getSrc() > current)
                        answer = calls.get(i).getSrc();
            for(int i=0; i<calls.size(); i++)
                if(calls.get(i).getDest() == UP)
                    if(calls.get(i).getDest() < answer && calls.get(i).getDest() > current)
                        answer = calls.get(i).getDest();
        }
        else //DOWN
        {
            answer = building.minFloor();
            for(int i=0; i<calls.size(); i++)
                if(calls.get(i).getType() == DOWN)
                    if(calls.get(i).getSrc() > answer && calls.get(i).getSrc() < current)
                        answer = calls.get(i).getSrc();
            for(int i=0; i<calls.size(); i++)
                if(calls.get(i).getDest() == DOWN)
                    if(calls.get(i).getDest() > answer && calls.get(i).getDest() < current)
                        answer = calls.get(i).getDest();
        }
        return answer;
    }

    private double checkTime(Elevator e, int start, int end)// check the basic time with time = distance / speed algorithm
    {
        int distance = dist(start, end);
        double speed = e.getSpeed();
        return ((distance/speed) + e.getStartTime() + e.getStopTime() + e.getTimeForOpen() + e.getTimeForClose());
        //every time we move to different floor, we have to open the doors, close the doors, start moving and stop moving.
        //each one takes time, so we add it to the final result
    }

    private int dist(int i, int j) //find distance
    {
        if(i>=j)
            return (i-j);
        else
            return (j-i);
    }


    @Override
    public void cmdElevator(int elev) //tell the elevator what to do
    {
        Elevator elevator = building.getElevetor(elev);
        LinkedList<CallForElevator> calls = list[elev].listOfCalls;

        if(calls.isEmpty()) //if elevator don't have calls, then stop
            return;
        if(calls.size()==1) //if elevator has only one call.
        {
            elevator.goTo(calls.getFirst().getSrc());
            elevator.goTo(calls.getFirst().getDest());
            calls.removeFirst();
        }
        int direction = checkDirection(calls, elevator.getPos());
        int floorsUP = checkAmountOfFloors(calls, UP);
        int floorsDOWN = checkAmountOfFloors(calls, DOWN);
        int nextFloor;

        if(direction==UP)
        {//if we are closer to the min src up floor then start there.
            nextFloor = findLowFloorUP(calls);
            if(nextFloor != building.maxFloor()) //there won't be any calls up from there. that is why i chose it as fail
            {//safe in case there are no up calls.
                elevator.goTo(nextFloor);
                floorsUP--; //already been to the first src floor, so we can decrese the amount of floors.
            }
            for (int i = 0; i < floorsUP; i++)
            {
                nextFloor = checkNextFloor(calls, direction, elevator.getPos());
                elevator.goTo(nextFloor);
            }
            direction = DOWN; //after going up we will start going down
            nextFloor = findHighFloorDOWN(calls);
            if(nextFloor != building.minFloor())//there won't be any calls up from there. that is why i chose it as fail
            {//safe in case there are no up calls.
                elevator.goTo(nextFloor);
                floorsDOWN--;
            }
            for (int i = 0; i < floorsDOWN; i++)
            {
                nextFloor = checkNextFloor(calls, direction, elevator.getPos());
                elevator.goTo(nextFloor);
            }
        }
        else //mirror image of the "up first" option
        {
            nextFloor = findHighFloorDOWN(calls);
            if(nextFloor != building.minFloor())
            {
                elevator.goTo(nextFloor);
                floorsDOWN--;
            }
            for (int i = 0; i < floorsDOWN; i++)
            {
                nextFloor = checkNextFloor(calls, direction, elevator.getPos());
                elevator.goTo(nextFloor);
            }
            direction = UP;
            nextFloor = findLowFloorUP(calls);
            if(nextFloor != building.maxFloor())
            {
                elevator.goTo(nextFloor);
                floorsUP--;
            }
            for (int i = 0; i < floorsUP; i++)
            {
                nextFloor = checkNextFloor(calls, direction, elevator.getPos());
                elevator.goTo(nextFloor);
            }
        }
        while (calls.isEmpty() == false)//remove all calls we finished
            calls.removeFirst();
    }

    /*
Code Owners,207689621,  Case,0,  Total waiting time: 218.9897426188186,  average waiting time per call: 21.89897426188186,  unCompleted calls,0,  certificate, -745326940


Code Owners,207689621,  Case,1,  Total waiting time: 475.9897426188186,  average waiting time per call: 47.59897426188186,  unCompleted calls,7,  certificate, -1741167892

Code Owners,207689621,  Case,2,  Total waiting time: 23234.79282212021,  average waiting time per call: 232.3479282212021,  unCompleted calls,36,  certificate, -7812782279

Code Owners,207689621,  Case,3,  Total waiting time: 292005.5382843333,  average waiting time per call: 730.0138457108333,  unCompleted calls,138,  certificate, -235227251921

Code Owners,207689621,  Case,4,  Total waiting time: 320916.4553686421,  average waiting time per call: 641.8329107372842,  unCompleted calls,112,  certificate, -93506367204

Code Owners,207689621,  Case,5,  Total waiting time: 1049428.12115705,  average waiting time per call: 1049.4281211570499,  unCompleted calls,481,  certificate, -164231085866687

Code Owners,207689621,  Case,6,  Total waiting time: 1036243.882096948,  average waiting time per call: 1036.243882096948,  unCompleted calls,457,  certificate, -125152871623255

Code Owners,207689621,  Case,7,  Total waiting time: 1359746.121157051,  average waiting time per call: 1359.746121157051,  unCompleted calls,702,  certificate, -1217806994013989

Code Owners,207689621,  Case,8,  Total waiting time: 1305859.8820969493,  average waiting time per call: 1305.8598820969494,  unCompleted calls,637,  certificate, -727721312695731

Code Owners,207689621,  Case,9,  Total waiting time: 1206807.3400743115,  average waiting time per call: 1206.8073400743115,  unCompleted calls,561,  certificate, -371137857440602


     */
}
