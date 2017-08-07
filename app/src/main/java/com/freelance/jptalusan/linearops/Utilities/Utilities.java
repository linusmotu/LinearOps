package com.freelance.jptalusan.linearops.Utilities;

import android.util.Log;

import com.freelance.jptalusan.linearops.R;
import com.freelance.jptalusan.linearops.Views.LinearOpsGridLayout;

import java.util.ArrayList;
import java.util.Collections;

public class Utilities {
    private static String TAG = "Utilities";
    private LinearOpsGridLayout l = null;
    private LinearOpsGridLayout r = null;

    public Utilities(LinearOpsGridLayout left, LinearOpsGridLayout right) {
        l = left;
        r = right;
    }

    public static ArrayList<Integer> getFactors(int number) {
        if (number % 2 != 0) {
            number++;
        }
        ArrayList<Integer> out = new ArrayList<>();
        out.add(1);
        out.add(number);
        for(int i = 2; i < number; i++){
            if (number % i == 0)
                out.add(i);
        }

        Collections.sort(out);
        String temp = "";
        for (int i : out) {
            temp += i + ",";
        }
        Log.d(TAG, number + ":" + temp);
        return out;
    }

    //TODO: Must refactor how i check for the children, since they are not redrawn and are not in the
    //places i expect them to be. right now it animates the invisible objects instead causing everything
    //to look weird.
    public boolean animateObjects(Equation eq, int userAnswer, boolean willAnimate) {
        int absAx;
        int absB;
        int absUserAnswer = Math.abs(userAnswer);

        double correctAnswer = eq.getX();

        boolean isCorrectSign = (userAnswer * -1) != correctAnswer;
        if (l.getTypeContainedIn().equals(Constants.POSITIVE_X) ||
                l.getTypeContainedIn().equals(Constants.NEGATIVE_X)) {
            absAx = Math.abs(l.getVisibleChildCount());
            absB  = Math.abs(r.getVisibleChildCount());
            l.setOneViewDrawables(l, r, isCorrectSign);
        } else {
            absAx = Math.abs(r.getVisibleChildCount());
            absB  = Math.abs(l.getVisibleChildCount());
            r.setOneViewDrawables(r, l, isCorrectSign);
        }
        Log.d(TAG, "absAx: " + absAx + ", " + "absB: " + absB);

        if (absUserAnswer > absB) {
            return false;
        }

        int delayFactor = willAnimate ? 1: 0;

        if (absAx == absB && absUserAnswer == 1) {
            for (int i = 0; i < absAx; ++i) {
                chooseWhichXToAnimate(i, 500 * i * delayFactor, absUserAnswer);
                chooseWhichOneToAnimate(i, 1000 * i * delayFactor);
            }
        } else if (absAx == 1) {
            for (int i = 0; i < absUserAnswer; ++i) {
                chooseWhichOneToAnimate(i, 0);
            }
            chooseWhichXToAnimate(0, 0, absUserAnswer);
        } else {
            int attemptToSolve = absB / absUserAnswer;
            int remainingChildren;
            if (r.getValuesInside().equals(Constants.ONE)) {
                remainingChildren = r.getVisibleChildCount();
            } else {
                remainingChildren = l.getVisibleChildCount();
            }
            int numberOfCircles = remainingChildren;
            int currentChild = 0;
            int outerLoop;
            int leftOverCircles = 0;
            if (absB % absUserAnswer != 0) {
                outerLoop = attemptToSolve + 1;
            } else {
                outerLoop = attemptToSolve;
            }

            Log.d(TAG, "RemainingChildren: " + remainingChildren);
            Log.d(TAG, "outerLoop: " + outerLoop);
            if (outerLoop > absAx) {
                //Compute for left over circles
                leftOverCircles = (outerLoop - absAx) * absUserAnswer;
                outerLoop = absAx;
            }

            /*
            * outerLoop = number of times the circles will be grouped and animated
            * into the boxes. So if outerLoop is less than absAx, then that is the number of boxes
            * left over without any circles inside.
            */

            /*
            * Outer loop will only reach valid circles, left over circles will be animated later.
             */

            for (int i = 0; i < outerLoop; ++i) {
                //Group the circles (remaining circles together)
                if (remainingChildren > absUserAnswer) {
                    remainingChildren -= absUserAnswer;
                    //Delay is based on outer loop so the circles will be animated together.
                    for (int j = 0; j < absUserAnswer; ++j) {
                        chooseWhichOneToAnimate(currentChild, 1000 * i * delayFactor);
                        currentChild++;
                    }
                    //Animate along with the current circles being animated
                    chooseWhichXToAnimate(i, 500 * i * delayFactor, absUserAnswer);
                /*
                * Animate the remaining circles (this means the user answer is wrong and not all
                * boxes have the same number of circles
                 */
                } else {
                    for (int j = 0; j < remainingChildren; ++j) {
                        chooseWhichOneToAnimate(currentChild, 1000 * i * delayFactor);
                        currentChild++;
                    }
                    chooseWhichXToAnimate(i, 500 * i * delayFactor, remainingChildren);
                }
            }
        }

        //TODO: add actions here after answer is checked
        if (correctAnswer == (double) userAnswer) {
            Log.w(TAG, "correct!");
            return true;
        } else {
            Log.w(TAG, "incorrect");
            return false;
        }
    }

    //Animate means to move one object from one layout to another layout
    private void chooseWhichOneToAnimate(int child, int delay) {
        if (r.getValuesInside().equals(Constants.ONE)) {
            r.animateOneView(child, delay);
        } else {
            l.animateOneView(child, delay);
        }
    }

    private void chooseWhichXToAnimate(int child, int delay, int dividend) {
        if (r.getValuesInside().equals(Constants.ONE)) {
            l.animateXView(child, delay, dividend);
        } else {
            r.animateXView(child, delay, dividend);
        }
    }

    private void chooseWhichXToPulse(int child, int delay, int dividend) {
        if (r.getValuesInside().equals(Constants.ONE)) {
            l.pulseXView(child, delay, dividend);
        } else {
            r.pulseXView(child, delay, dividend);
        }
    }

    private void chooseWhichOneToPulse(int child, int delay) {
        if (r.getValuesInside().equals(Constants.ONE)) {
            r.pulseOneView(child, delay);
        } else {
            l.pulseOneView(child, delay);
        }
    }

    public static String getTypeFromResource(int imageResource) {
        switch (imageResource) {
            case R.drawable.white_box:
                return Constants.POSITIVE_X;
            case R.drawable.black_box:
                return Constants.NEGATIVE_X;
            case R.drawable.white_circle:
                return Constants.POSITIVE_1;
            case R.drawable.black_circle:
                return Constants.NEGATIVE_1;
            default:
                return "";
        }
    }

    public static String getOppositeType(String type) {
        switch (type) {
            case Constants.POSITIVE_X:
                return Constants.NEGATIVE_X;
            case Constants.NEGATIVE_X:
                return Constants.POSITIVE_X;
            case Constants.POSITIVE_1:
                return Constants.NEGATIVE_1;
            case Constants.NEGATIVE_1:
                return Constants.POSITIVE_1;
            default:
                return "";
        }
    }

    public static String getOneOrX(int imageResource) {
        switch (imageResource) {
            case R.drawable.white_box:
            case R.drawable.black_box:
                return "X";
            case R.drawable.white_circle:
            case R.drawable.black_circle:
                return "1";
            default:
                return "";
        }
    }

    public static int determineResetPeriodInMillis(LinearOpsGridLayout l, LinearOpsGridLayout r, int userAnswer, Equation eq) {
        int xCount = 0;
        int oneCount = 0;
        int quotient;
        int remainder;
        int mUserAnswer = Math.abs(userAnswer);
        if (l.getValuesInside().equals(Constants.ONE)) {
            oneCount = Math.abs(l.getCountOfTypeContainedIn());
            xCount = Math.abs(r.getCountOfTypeContainedIn());
        } else if (r.getValuesInside().equals(Constants.ONE)) {
            oneCount = Math.abs(r.getCountOfTypeContainedIn());
            xCount = Math.abs(l.getCountOfTypeContainedIn());
        }
        if (userAnswer == 0) { //Empty answer
            return Constants.DEFAULT_RESET;
        }
        //Correct answer
        if (userAnswer == eq.getX()) {
            Log.d(TAG, "Correct reset: " + (Constants.RESET_FACTOR * xCount) + " ms");
            return Constants.RESET_FACTOR * xCount;
        }
        //Answer is greater than number of One's
        if (mUserAnswer > oneCount) {
            Log.d(TAG, "Default reset: Constants.DEFAULT_RESET ms");
            return Constants.DEFAULT_RESET;
        }
        //Should now take into account the empty boxes as well
        Log.d(TAG, "Exceed/match x reset: " + (xCount * Constants.RESET_FACTOR) + " ms");
        return xCount * Constants.RESET_FACTOR;// + 2000; //have to add 2000 for the left over balls
    }

    public static void performCleanup(LinearOpsGridLayout left, LinearOpsGridLayout right) {
        left.performCleanup();
        right.performCleanup();
    }
}
