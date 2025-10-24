package test;

public class testCore
{

    public static void run (testVariations variation) {
        essentialTesting eTesting = new essentialTesting();
        additionalTesting aTesting = new additionalTesting();
        switch (variation) {
            case ESSENTIALS_ONLY -> eTesting.run();
            case ADDITIONAL_ONLY -> aTesting.run();
            case ALL -> {eTesting.run(); aTesting.run();}
            default -> {}
        }

    }

}
