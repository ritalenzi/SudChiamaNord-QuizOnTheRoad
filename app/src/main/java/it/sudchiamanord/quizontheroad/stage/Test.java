package it.sudchiamanord.quizontheroad.stage;

/**
 * Lists all the possible tests used to pass a stage
 */
public enum Test
{
    text (0),
    audio (1),
    photo (2),
    video (3),
    no_media (4);

    Test (int value)
    {
        this.value = value;
    }

    public static Test getTest (int value)
    {
        for (Test st : values()) {
            if (st.value == value) {
                return st;
            }
        }

        return no_media;
    }

    private final int value;
}
