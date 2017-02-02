package nord.chiama.sud.caccia.stage;

public class Stage
{
    private int number;
    private String serverId;
    private Status status;
    private String locationClue; // clue to be used to find the location
    private String message;
    private String multimediaClue;     // clue to be used to know how to create the multimedia answer
    private Test test;
    private boolean correctPosition;
    private boolean waitPositionConfirmed;

    public void setStatus (String s)
    {
        status = Status.valueOf (s);
    }

    public void setStatus (Status s)
    {
        status = s;
    }

    public Status getStatus()
    {
        return status;
    }

    public void setServerId (String s)
    {
        serverId = s;
    }

    public String getServerId()
    {
        return serverId;
    }

    public void setNumber (int n)
    {
        number = n;
    }

    public int getNumber()
    {
        return number;
    }

    public void setLocationClue (String c)
    {
        locationClue = c;
    }

    public String getLocationClue()
    {
        return locationClue;
    }

    public void setMultimediaClue (String c)
    {
        multimediaClue = c;
    }

    public String getMultimediaClue()
    {
        return multimediaClue;
    }

    public void setTest (Test s)
    {
        test = s;
    }

    public void setTest (String s)
    {
        test = Test.valueOf (s);
    }

    public Test getTest()
    {
        return test;
    }

    public void setCorrectPosition (boolean b)
    {
        correctPosition = b;
    }

    public boolean isPositionCorrect()
    {
        return correctPosition;
    }

    public void setWaitPositionConfirmed (boolean b)
    {
        waitPositionConfirmed = b;
    }

    public boolean waitForPositionConfirmed()
    {
        return waitPositionConfirmed;
    }
}
