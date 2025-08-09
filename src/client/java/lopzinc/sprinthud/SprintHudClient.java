package lopzinc.sprinthud;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import lopzinc.sprinthud.storage.storage;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SprintHudClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MinecraftClient client = MinecraftClient.getInstance();
		HudRenderCallback.EVENT.register((drawContext, tickDeltaManager) -> {
			if (storage.hudEnabled()) {
				ClientPlayerEntity player = client.player;
				if (player!=null) {
					boolean sprinting = false;
					String mode = null;
					boolean holdMode = client.options.getSprintToggled().getValue();
					boolean isSprinting = player.isSprinting();
					boolean keyDown = client.options.sprintKey.isPressed();

					boolean displaySprintMode = true;
					String format = "§7Sprint: %status%";
					try {
						BufferedReader reader = new BufferedReader(new FileReader((FabricLoader.getInstance().getConfigDir().resolve("simplesprinthud.txt")).toFile()));
						//code for custom format
						String data = reader.readLine();
						format = data.split(":",3)[2];

						//code for displaying mode
						displaySprintMode = Boolean.parseBoolean(data.split(":")[1]);
					} catch (IOException e) { e.printStackTrace(); }

					if (holdMode && (keyDown | isSprinting)) {// if toggled
						sprinting = true;
						if (displaySprintMode) { mode = "Toggled"; }

					} else if (isSprinting | keyDown){//holding mode
						sprinting = true;
						if (displaySprintMode) { mode = "Holding"; }
					}

					String hudText = format.replaceAll("&&","§").replaceAll("%status%",((sprinting ? "§aON" : "§cOFF") + (mode!=null ? " (" + mode + ")" : "")));

					drawContext.drawText(client.textRenderer, hudText, configStore.x, configStore.y, 0xFFFFFFFF, true);
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
													configStore.x = Integer.parseInt(coords[0]);
													configStore.y = Integer.parseInt(coords[1]);
													configStore.save();
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
										storage.cycleHudState();
										client.player.sendMessage(Text.literal("§8[§3Sprint§8] §7Toggled sprint hud " + (storage.hudEnabled() ? "§aon" : "§coff") + "§7."),false);
										return Command.SINGLE_SUCCESS;
									}))
							.then(ClientCommandManager.literal("format")
									.then(ClientCommandManager.argument("%status% to write on/off, && for colour code.", StringArgumentType.greedyString())
											.executes(context -> {
												String input = StringArgumentType.getString(context, "%status% to write on/off, && for colour code.");
												if (input.contains("%status%")) {
													configStore.format(input);
												} else {
													client.player.sendMessage(Text.literal("§8[§3Sprint§8] §7 No %status% detected, if you wish to disable the hud, do /sprint toggle."),false);
												}
												return Command.SINGLE_SUCCESS;
											})
									)
							)
							.then(ClientCommandManager.literal("toggleModeDisplay")
									.executes(context -> {
										boolean newState = configStore.CycleHud();
										client.player.sendMessage(Text.literal("§8[§3Sprint§8] §7Mode display set to " + (newState ? ("§aON") : ("§cOFF")) + "§7."),false);
										return Command.SINGLE_SUCCESS;
									})
							)
			);
		});
	}
}