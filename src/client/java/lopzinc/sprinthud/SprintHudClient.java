
package lopzinc.sprinthud;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;


import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SprintHudClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		Config config = configLoader.loadConfig();
		MinecraftClient client = MinecraftClient.getInstance();

		HudRenderCallback.EVENT.register((drawContext, tickDeltaManager) -> {
			if (config.hudEnabled) {
				ClientPlayerEntity player = client.player;
				if (player!=null) {
					boolean sprinting = false;
					String mode = null;
					boolean holdMode = client.options.getSprintToggled().getValue();
					boolean isSprinting = player.isSprinting();
					boolean keyDown = client.options.sprintKey.isPressed();
					if (holdMode && (keyDown | isSprinting)) {// if toggled
						sprinting = true;
						if (config.modeDisplay) { mode = "Toggled"; }
					} else if (isSprinting | keyDown){//holding mode
						sprinting = true;
						if (config.modeDisplay) { mode = "Holding"; }
					}
					String hudText = config.format.replaceAll("&&","§").replaceAll("%status%",((sprinting ? "§aON" : "§cOFF") + (mode!=null ? " (" + mode + ")" : "")));
					drawContext.drawText(client.textRenderer, hudText, config.x, config.y, 0xFFFFFFFF, true);
				}
			}
		});
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(
					ClientCommandManager.literal("sprint")
							.then(ClientCommandManager.literal("move")
									.then(ClientCommandManager.argument("coords (x,y)", StringArgumentType.greedyString())
											.executes(context -> {
												String coordinates = StringArgumentType.getString(context, "coords (x,y)");
												Pattern pattern = Pattern.compile("^\\d+,\\d+$");
												Matcher m = pattern.matcher(coordinates);
												if (m.matches()) {
													String[] coords = coordinates.split(",");
													config.x = Integer.parseInt(coords[0]);
													config.y = Integer.parseInt(coords[1]);
													configLoader.saveConfig(config);
													client.player.sendMessage(Text.literal("§8[§3Sprint§8] §7Set coordinates to §6" + coordinates + "§7."), false);
												} else {
													client.player.sendMessage(Text.literal("§8[§3Sprint§8] §cInvalid argument, use the format x,y."), false);
												}
												return Command.SINGLE_SUCCESS;
											})
									)
							)
							.then(ClientCommandManager.literal("toggleHud")
									.executes(context -> {
										config.hudEnabled = !config.hudEnabled;
										configLoader.saveConfig(config);
										client.player.sendMessage(Text.literal("§8[§3Sprint§8] §7Toggled sprint hud " + (config.hudEnabled ? "§aon" : "§coff") + "§7."),false);
										return Command.SINGLE_SUCCESS;
									}))
							.then(ClientCommandManager.literal("format")
									.then(ClientCommandManager.argument("%status% to write on/off, && for colour code.", StringArgumentType.greedyString())
											.executes(context -> {
												String input = StringArgumentType.getString(context, "%status% to write on/off, && for colour code.");
												if (input.contains("%status%")) {
													config.format = input;
													configLoader.saveConfig(config);
													client.player.sendMessage(Text.literal("§8[§3Sprint§8] §7Status updated!"),false);
												} else {
													client.player.sendMessage(Text.literal("§8[§3Sprint§8] §7 No %status% detected, if you wish to disable the hud, do /sprint toggle."),false);
												}
												return Command.SINGLE_SUCCESS;
											})
									)
							)
							.then(ClientCommandManager.literal("toggleModeDisplay")
									.executes(context -> {
										config.modeDisplay = !config.modeDisplay;
										configLoader.saveConfig(config);
										client.player.sendMessage(Text.literal("§8[§3Sprint§8] §7Mode display set to " + (config.modeDisplay ? ("§aON") : ("§cOFF")) + "§7."),false);
										return Command.SINGLE_SUCCESS;
									})
							)
			);
		});
	}
}