package com.kvxd.tormod.mixin;

import com.kvxd.tormod.TorMod;
import com.kvxd.tormod.utils.TorRunner;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin extends Screen {

    protected MultiplayerScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        int textColor = TorMod.config.getEnabled() ? Colors.GREEN : Colors.RED;

        ButtonWidget enabledButton = ButtonWidget.builder(Text.translatable("text.tormod.title"), btn -> {
                    TorMod.config.setEnabled(!TorMod.config.getEnabled());
                    if (TorMod.config.getEnabled())
                        TorRunner.INSTANCE.startTor();
                    else
                        TorRunner.INSTANCE.stopTor();

                    btn.setMessage(Text.translatable("text.tormod.title").styled(style -> style.withColor(TorMod.config.getEnabled() ? Colors.GREEN : Colors.RED)));
                })
                .position(5, 5)
                .width(60)
                .build();

        enabledButton.setMessage(Text.translatable("text.tormod.title").styled(style -> style.withColor(textColor)));

        addDrawableChild(enabledButton);

        ButtonWidget refreshButton = ButtonWidget.builder(Text.translatable("text.tormod.refresh"), btn -> {
                    TorRunner.INSTANCE.requestNewExitNode();
                })
                .position(5 + 60 + 5, 5)
                .width(80)
                .build();

        if (!TorMod.Companion.getUseSystem())
            addDrawableChild(refreshButton);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        String statusText = "Status: " + TorRunner.INSTANCE.getStatus();
        int textWidth = client.textRenderer.getWidth(statusText);
        int margin = 5;
        int x = this.width - textWidth - margin;
        if (!TorMod.Companion.getUseSystem())
            context.drawText(client.textRenderer, statusText, x, margin, -1, true);
    }

}
