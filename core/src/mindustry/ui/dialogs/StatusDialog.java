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
    public Seq<Unit> selected = new Seq<>();
    public boolean selectUnits = false;
    public Vec2 selectStart;

    public StatusDialog(){
        super("");

        addCloseButton();

        buttons.button(Icon.add, () -> {
            apply();
        }).width(100).get().addListener(new Tooltip(t -> t.background(Tex.button).add("Apply Effect")));

        Label label = cont.label(() -> status.localizedName).get();
        cont.row();

        cont.pane(list -> {
            for(int i = 0;i < content.statusEffects().size;i++){
                StatusEffect effect = content.statusEffects().get(i);
                if(effect.isHidden()) continue;
                if(i % 10 == 0) list.row();
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

        ImageButton selectButton = cont.button(new TextureRegionDrawable(Icon.units), () -> {
            hide();
            selectUnits = true;
            selected.clear();
        }).width(150).get();
        selectButton.addListener(new Tooltip(t -> t.background(Tex.button).add("Select Units")));
        cont.row();

        Events.run(Trigger.update, () -> {
            if(!Core.input.justTouched() || Core.scene.hasMouse() || !selectUnits) return;
            if(selectStart == null) selectStart = new Vec2(Core.input.mouseWorldX(), Core.input.mouseWorldY());
            else{
                float x = selectStart.x, y = selectStart.y, mx = Core.input.mouseWorldX(), my = Core.input.mouseWorldY();
                Groups.unit.intersect(Math.min(x, mx), Math.min(y, my) , Math.abs(mx - x), Math.abs(my - y), u -> {
                    if(u.team == player.team()) selected.add(u);
                });
                selectUnits = false;
                selectStart = null;
                show();
            }
        });

        Events.on(WorldLoadEvent.class, event -> {
            selected.clear();
            selectUnits = false;
            selectStart = null;
        });
    }

    public void apply(){
        if(net.client()){
            StringBuilder builder = new StringBuilder();
            Call.sendChatMessage(builder.toString());
        }else{
            Seq<Unit> valid = new Seq<>();
            for(Unit u : selected) if(u.isValid()) valid.add(u);
            for(Unit u : valid) u.apply(status, statusTime * 60);
            selected = valid;
            hide();
        }
    }
}
