package mindustry.ui.fragments;

import arc.scene.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.ui.*;

import static mindustry.Vars.*;

public class ToggleFragment{
    public void build(Group parent){
        parent.fill(table -> {
            table.table(Tex.button, togglables -> {
                togglables.pane(list -> {
                    ImageButton lighting = new ImageButton(new TextureRegionDrawable(Blocks.illuminator.fullIcon), Styles.emptyTogglei);
                    lighting.clicked(() -> {
                        mapLighting = !mapLighting;
                    });
                    lighting.addListener(new Tooltip(t -> t.background(Tex.button).add("Disabled Lighting")));
                    lighting.update(() -> lighting.setChecked(!mapLighting));
                    list.add(lighting).size(32).pad(10);

                    ImageButton overdrive = new ImageButton(new TextureRegionDrawable(Blocks.overdriveProjector.fullIcon), Styles.emptyTogglei);
                    overdrive.clicked(() -> {
                        overdriveRanges = !overdriveRanges;
                    });
                    overdrive.addListener(new Tooltip(t -> t.background(Tex.button).add("Overdrive Ranges")));
                    overdrive.update(() -> overdrive.setChecked(overdriveRanges));
                    list.add(overdrive).size(32).pad(10);

                    ImageButton turret = new ImageButton(new TextureRegionDrawable(Blocks.duo.fullIcon), Styles.emptyTogglei);
                    turret.clicked(() -> {
                        turretRanges = !turretRanges;
                    });
                    turret.addListener(new Tooltip(t -> t.background(Tex.button).add("Turret Ranges")));
                    turret.update(() -> turret.setChecked(turretRanges));
                    list.add(turret).size(32).pad(10);

                    ImageButton unit = new ImageButton(new TextureRegionDrawable(UnitTypes.dagger.fullIcon), Styles.emptyTogglei);
                    unit.clicked(() -> {
                        unitRanges = !unitRanges;
                    });
                    unit.addListener(new Tooltip(t -> t.background(Tex.button).add("Unit Ranges")));
                    unit.update(() -> unit.setChecked(unitRanges));
                    list.add(unit).size(32).pad(10);

                    list.button(Icon.cancel, () -> {
                        showToggles = false;
                    }).size(48);

                    list.row();

                    ImageButton freecam = new ImageButton(new TextureRegionDrawable(UnitTypes.gamma.fullIcon), Styles.emptyTogglei);
                    freecam.clicked(() -> {
                        freeCam = !freeCam;
                    });
                    freecam.addListener(new Tooltip(t -> t.background(Tex.button).add("Freecam Mode")));
                    freecam.update(() -> freecam.setChecked(freeCam));
                    list.add(freecam).size(32).pad(10);

                    ImageButton miner = new ImageButton(new TextureRegionDrawable(UnitTypes.mono.fullIcon), Styles.emptyTogglei);
                    miner.clicked(() -> {
                        minerAI = !minerAI;
                    });
                    miner.addListener(new Tooltip(t -> t.background(Tex.button).add("Mining AI")));
                    miner.update(() -> miner.setChecked(minerAI));
                    list.add(miner).size(32).pad(10);

                    ImageButton rebuild = new ImageButton(new TextureRegionDrawable(UnitTypes.poly.fullIcon), Styles.emptyTogglei);
                    rebuild.clicked(() -> {
                        rebuildAI = !rebuildAI;
                    });
                    rebuild.addListener(new Tooltip(t -> t.background(Tex.button).add("Rebuild AI")));
                    rebuild.update(() -> rebuild.setChecked(rebuildAI));
                    list.add(rebuild).size(32).pad(10);

                    ImageButton helper = new ImageButton(new TextureRegionDrawable(UnitTypes.oct.fullIcon), Styles.emptyTogglei);
                    helper.clicked(() -> {
                        helperAI = !helperAI;
                    });
                    helper.addListener(new Tooltip(t -> t.background(Tex.button).add("Helper AI")));
                    helper.update(() -> helper.setChecked(helperAI));
                    list.add(helper).size(32).pad(10);

                    ImageButton target = new ImageButton(new TextureRegionDrawable(UnitTypes.beta.fullIcon), Styles.emptyTogglei);
                    target.clicked(() -> {
                        autoTarget = !autoTarget;
                    });
                    target.addListener(new Tooltip(t -> t.background(Tex.button).add("Autotarget")));
                    target.update(() -> target.setChecked(autoTarget));
                    list.add(target).size(32).pad(10);
                }).top().center();
                togglables.visible(() -> ui.hudfrag.shown && showToggles);
            });
        });
    }
}
