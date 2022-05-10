package mindustry.ui.dialogs;


import arc.*;
import arc.math.geom.*;
import arc.scene.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.fragments.*;

import static mindustry.Vars.*;

public class StatusDialog extends BaseDialog{
    public ImageButton hudButton;
    public StatusEffect status = StatusEffects.burning;
    public float statusTime = 10;

    public StatusDialog(){
        super("");

        addCloseButton();

        buttons.button(Icon.add, () -> {
            apply();
        }).width(100).get().addListener(new Tooltip(t -> t.background(Tex.button).add("Apply Effect (To Commanded Units)")));

        Label label = cont.label(() -> status.localizedName).get();
        cont.row();

        Seq<StatusEffect> all = content.statusEffects().select(s -> !s.isHidden());
        cont.pane(list -> {
            for(int i = 0;i < all.size;i++){
                StatusEffect effect = all.get(i);
                if(i != 0 && i % 10 == 0) list.row();
                list.button(new TextureRegionDrawable(effect.uiIcon), () -> {
                    status = effect;
                    hudButton.getStyle().imageUp = new TextureRegionDrawable(effect.uiIcon);
                    label.setText(effect.localizedName);
                }).size(64);
            }
        }).width(800).top().center().get();
        cont.row();

        cont.table(table -> {
            table.defaults().left();
            table.field(Strings.autoFixed(statusTime, 2), in -> {
                try{
                    statusTime = Float.parseFloat(in);
                }catch(NumberFormatException n){
                    statusTime = 10f;
                }
            }).width(100);
            table.image(new TextureRegionDrawable(Icon.refresh)).get().addListener(new Tooltip(t -> t.background(Tex.button).add("Status Time")));
        }).center().bottom();
        cont.row();
    }

    public void apply(){
        if(net.client()){
            StringBuilder builder = new StringBuilder();
            Call.sendChatMessage(builder.toString());
        }else{
            for(Unit u : control.input.selectedUnits) if(u.isValid()) u.apply(status, statusTime * 60);
            player.unit().apply(status, statusTime * 60);
            hide();
        }
    }
}
