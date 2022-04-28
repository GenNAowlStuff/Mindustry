package mindustry.ui.fragments;

import arc.*;
import arc.graphics.*;
import arc.scene.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;

import static mindustry.Vars.*;

/**
 * Somewhat based off of MeepOfFaith/Testing-Utilities. Also contains a ported version of sk7725/Time Control. Where do I even credit mod authors?
 */
public class UtilitiesFragment extends Fragment{
    public TeamChooseDialog chooseDialog;
    public float killPresst, dupePresst;
    public float current = 1;
    public float[] speeds = new float[]{0.25f, 0.5f, 1f, 2f, 4f}, speeds2 = new float[]{0f, 0.1f, -1, 8f, 16f};

    @Override
    public void build(Group parent){
        parent.fill(cont -> {
            cont.bottom().left();

            cont.table(top -> {
                top.table(Styles.black5, table -> {
                    table.background(Tex.pane).margin(5);
                    Button fill = table.button(new TextureRegionDrawable(Blocks.coreShard.fullIcon), Styles.emptyi, 32, () -> {
                        fillCore();
                    }).size(48).padRight(-4).get();
                    fill.addListener(new Tooltip(t -> t.background(Tex.button).add("Fill Core")));

                    ImageButton sandbox = table.button(new TextureRegionDrawable(Icon.defense), Styles.emptytogglei, () -> {
                        toggleSandbox();
                    }).size(48).padRight(-4).get();
                    sandbox.update(() -> sandbox.setChecked(state.rules.infiniteResources));
                    sandbox.addListener(new Tooltip(t -> t.background(Tex.button).add("Toggle Sandbox")));
                }).left().padRight(-4);

                top.table(Styles.black5, table -> {
                    table.background(Tex.buttonEdge3).margin(5);

                    Image unitIcon = table.image((player.unit() == null ? UnitTypes.dagger : player.unit().type).fullIcon).size(48).padRight(-4).get().setScaling(Scaling.fit);

                    Button kill = table.button(Icon.cancel, Styles.emptyi, () -> killUnit()).size(48).padRight(-8).get();
                    kill.update(() -> {
                        if(kill.isPressed()){
                            killPresst += Time.delta;
                            if(killPresst > pressTime) killUnit();
                        }else killPresst = 0;
                    });
                    kill.addListener(new Tooltip(t -> t.background(Tex.button).add("Kill Unit")));

                    Button dupe = table.button(Icon.copy, Styles.emptyi, () -> dupeUnit()).size(48).get();
                    dupe.update(() -> {
                        if(dupe.isPressed()){
                            dupePresst += Time.delta;
                            if(dupePresst > pressTime) dupeUnit();
                        }else dupePresst = 0;
                    });
                    dupe.addListener(new Tooltip(t -> t.background(Tex.button).add("Duplicate Unit")));

                    Events.on(UnitChangeEvent.class, l -> {
                        unitIcon.setDrawable((player.unit() == null ? UnitTypes.dagger : player.unit().type).fullIcon);
                    });

                    TextureRegionDrawable teamRect = new TextureRegionDrawable(Core.atlas.find("blank")){
                        @Override
                        public Drawable tint(Color c){
                            tint.set(c);
                            return null;
                        }
                    };
                    teamRect.tint(player.team().color);
                    chooseDialog = new TeamChooseDialog("", team -> {
                        changeTeam(team);
                        teamRect.tint(player.team().color);
                    });

                    table.button(teamRect, Styles.emptyi, 32, () -> {
                        chooseDialog.show();
                    }).padRight(8).get().addListener(new Tooltip(t -> t.background(Tex.button).add("Change Team")));
                }).left();
            }).visible(() -> ui.hudfrag.shown).left().padLeft(-4).padBottom(-4);

            cont.row();

            cont.table(Styles.black5, table -> {
                table.background(Tex.buttonEdge3);
                for(int i = 0;i < speeds.length;i++){
                    if(speeds2[i] != -1) addSpeed2(table, speeds[i], speeds2[i]).width(65);
                    else addSpeed(table, speeds[i]).width(65);
                }
            }).visible(() -> ui.hudfrag.shown).left().padLeft(-4).padBottom(-4);
        });
    }

    public void fillCore(){
        content.items().each(i -> player.team().core().items.set(i, player.team().core().storageCapacity));
    }

    public void toggleSandbox(){
        state.rules.infiniteResources = !state.rules.infiniteResources;
    }

    public void killUnit(){
        if(player.unit() != null) player.unit().damage(Float.MAX_VALUE);
    }

    public void dupeUnit(){
        if(player.unit() != null){
            Unit unit = player.unit().type.create(player.team());
            unit.set(player.unit().x, player.unit().y);
            unit.add();
        }
    }

    public void changeTeam(Team team){
        player.team(team);
    }

    public Cell<Button> addSpeed(Table table, float speed){
        var b = new Button(Styles.logict);
        b.label(() -> (current == speed ? "[sky]" : "[white]") + "x" + Strings.autoFixed(speed, 3) + "[]");
        b.clicked(() -> {
            Time.setDeltaProvider(() -> Math.min(Core.graphics.getDeltaTime() * 60 * speed, 3 * speed));
            current = speed;
        });
        return table.add(b).size(50, 40).color(Pal.lancerLaser).pad(1);
    }

    public Cell<Button> addSpeed2(Table table, float speed, float speed2){
        var b = new Button(Styles.logict);
        b.label(() -> (current == speed ? "[sky]" : (current == speed2 ? "[accent]" : "[white]")) + "x" + Strings.autoFixed(current == speed2 ? speed2 : speed, 3) + "[]");
        b.clicked(() -> {
            if(current == speed){
                Time.setDeltaProvider(() -> Math.min(Core.graphics.getDeltaTime() * 60 * speed2, 3 * speed2));
                current = speed2;
                b.setColor(Pal.accent);
            }else{
                Time.setDeltaProvider(() -> Math.min(Core.graphics.getDeltaTime() * 60 * speed, 3 * speed));
                current = speed;
                b.setColor(Pal.lancerLaser);
            }
        });
        b.update(() -> {
            b.setColor(current == speed2 ? Pal.accent : Pal.lancerLaser);
        });
        return table.add(b).size(50, 40).color(Pal.lancerLaser).pad(1);
    }
}
