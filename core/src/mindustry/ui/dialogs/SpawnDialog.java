package mindustry.ui.dialogs;

import arc.*;
import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.fragments.*;

import static mindustry.Vars.*;

/**
 * A ported version of DeltaNedas/Unit Factory. Please don't kill me
 */
public class SpawnDialog extends BaseDialog{
    public ImageButton hudButton;
    public TeamChooseDialog chooseDialog;
    public UnitType spawn = UnitTypes.dagger;
    public Team spawnTeam = Team.sharded;
    public Vec2 spawnPos = new Vec2();
    public float spawnRand = 0.5f;
    public int spawnNum = 1;
    public boolean selectPos = false;

    public SpawnDialog(){
        super("");

        addCloseButton();

        buttons.button(Icon.modeAttack, () -> {
            spawn();
        }).width(100).get().addListener(new Tooltip(t -> t.background(Tex.button).add("Spawn Unit")));

        TextureRegionDrawable teamRect = new TextureRegionDrawable(Core.atlas.find("blank")){
            @Override
            public Drawable tint(Color c){
                tint.set(c);
                return null;
            }
        };
        teamRect.tint(spawnTeam.color);
        chooseDialog = new TeamChooseDialog("", team -> {
            spawnTeam = team;
            teamRect.tint(spawnTeam.color);
        });
        buttons.button(teamRect, 40, () -> {
            chooseDialog.show();
        }).width(100).get().addListener(new Tooltip(t -> t.background(Tex.button).add("Change Team")));

        buttons.button(new TextureRegionDrawable(Blocks.coreShard.fullIcon), () -> {
            core();
            hide();
        }).width(100).get().addListener(new Tooltip(t -> t.background(Tex.button).add("Place Core")));

        Label label = cont.label(() -> spawn.localizedName).get();
        cont.row();

        cont.pane(list -> {
            for(int i = 0;i < content.units().size;i++){
                UnitType unit = content.units().get(i);
                if(unit.isHidden()) continue;
                if(i % 5 == 0) list.row();
                list.button(new TextureRegionDrawable(unit.fullIcon), () -> {
                    spawn = unit;
                    hudButton.getStyle().imageUp = new TextureRegionDrawable(unit.fullIcon);
                    label.setText(spawn.localizedName);
                }).size(128);
            }
        }).width(700).top().center().get();
        cont.row();

        cont.table(table -> {
            table.defaults().left();
            table.field("" + spawnNum, in -> {
                try{
                    spawnNum = Integer.parseInt(in);
                }catch(NumberFormatException n){
                    spawnNum = 1;
                }
            }).width(100);
            table.image(new TextureRegionDrawable(Icon.units)).get().addListener(new Tooltip(t -> t.background(Tex.button).add("Unit Count")));
        }).center().bottom();
        cont.row();

        cont.table(table -> {
            table.defaults().left();
            table.field(Strings.autoFixed(spawnRand, 2), in -> {
                try{
                    spawnRand = Float.parseFloat(in);
                }catch(NumberFormatException n){
                    spawnRand = 0.5f;
                }
            }).width(100);
            table.image(new TextureRegionDrawable(Icon.resize)).get().addListener(new Tooltip(t -> t.background(Tex.button).add("Spawn Spread")));
        }).center().bottom();
        cont.row();

        TextButton posButton = cont.button("(" + Mathf.round(spawnPos.x / tilesize) + ", " + Mathf.round(spawnPos.y / tilesize) + ")", () -> {
            hide();
            selectPos = true;
        }).width(150).get();
        posButton.addListener(new Tooltip(t -> t.background(Tex.button).add("Set Position")));
        cont.row();

        Events.run(Trigger.update, () -> {
            if(!Core.input.justTouched() || Core.scene.hasMouse() || !selectPos) return;
            selectPos = false;
            spawnPos.set(Core.input.mouseWorld());
            posButton.setText("(" + Mathf.round(spawnPos.x / tilesize) + ", " + Mathf.round(spawnPos.y / tilesize) + ")");
            show();
        });

        Events.on(WorldLoadEvent.class, event -> {
            selectPos = false;
        });
    }

    public void spawn(){
        if(net.client()){
            StringBuilder builder = new StringBuilder();
            builder.append("/js ");
            builder.append("for(var i = 0;i < ").append(spawnNum).append(";i++){");
            builder.append("Tmp.v1.rnd(").append(Mathf.random(spawnRand * tilesize)).append(");");
            builder.append("var u = UnitTypes.").append(spawn.name).append(".create(").append("Team.").append(spawnTeam.name).append(");");
            builder.append("u.set(").append(spawnPos.x).append(" + Tmp.v1.x,").append(spawnPos.y).append(" + Tmp.v1.y);");
            builder.append("u.add()");
            builder.append("}");
            Log.info(builder.toString());
            Call.sendChatMessage(builder.toString());
        }else{
            hide();
            for(var n = 0;n < spawnNum;n++){
                Tmp.v1.rnd(Mathf.random(spawnRand * tilesize));

                var unit = spawn.create(spawnTeam);
                unit.set(spawnPos.x + Tmp.v1.x, spawnPos.y + Tmp.v1.y);
                unit.add();
            }
        }
    }

    public void core(){
        if(net.client()){
            StringBuilder builder = new StringBuilder();
            builder.append("/js ");
            builder.append("world.tileWorld(");
            builder.append(spawnPos.x).append(", ").append(spawnPos.y);
            builder.append(").setBlock(Blocks.coreShard, ");
            builder.append("Team.").append(spawnTeam.name).append(");");
            Log.info(builder.toString());
            Call.sendChatMessage(builder.toString());
        }else world.tileWorld(spawnPos.x, spawnPos.y).setBlock(Blocks.coreShard, spawnTeam);
    }
}