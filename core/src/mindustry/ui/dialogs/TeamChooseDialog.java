package mindustry.ui.dialogs;

import arc.func.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.net.Administration.*;
import mindustry.ui.*;

import static mindustry.Vars.*;

public class TeamChooseDialog extends BaseDialog{
    public Cons<Team> setter;

    public TeamChooseDialog(String title, Cons<Team> setter){
        super(title);
        this.setter = setter;

        setup();
        shown(this::setup);
        addCloseButton();
    }

    private void setup(){
        cont.clear();

        cont.pane(list -> {
            for(Team team : Team.baseTeams){
                list.button("[#" + team.color.toString() + "]" + team.localized(), Styles.defaultt, () -> {
                    setter.get(team);
                    hide();
                }).width(200);
                list.row();
            }
        });
    }
}
