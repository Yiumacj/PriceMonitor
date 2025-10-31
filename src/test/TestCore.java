package test;

public class TestCore
{

    public static void run (TestVariations variation) {
        EssentialTesting eTesting = new EssentialTesting();
        AdditionalTesting aTesting = new AdditionalTesting();
        switch (variation) {
            case ESSENTIALS_ONLY -> eTesting.run();
            case ADDITIONAL_ONLY -> aTesting.run();
            case ALL -> {eTesting.run(); aTesting.run();}
            default -> {}
        }

    }

}
