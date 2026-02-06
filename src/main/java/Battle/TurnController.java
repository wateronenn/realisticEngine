package Battle;

import Action.Action;

public class TurnController {
    public static void execute(Action action){
        if(action != null){
            action.execute();
        }
    }
}
