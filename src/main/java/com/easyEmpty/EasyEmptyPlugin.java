package com.easyEmpty;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ClientTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

import java.util.Objects;

@Slf4j
@PluginDescriptor(
	name = "Easy Empty",
	description = "Swap essence pouch left click to Empty when near a runecrafting altar",
	tags = {"swap","swapper","menu","entry","menu entry swapper","runecrafting","pouch","essence"}
)
public class EasyEmptyPlugin extends Plugin
{
	int[] altars = {
		10571, // Earth
		10315, // Fire
		10827, // Water
		11339, // Air
		10059, // Body
		11083, // Mind
		8523,  // Cosmic
		9035,  // Chaos
		9803,  // Law
		9547,  // Nature
		8779,  // Death
		9291, // Wrath
		8508, // Astral
		12875 // Blood
	};

	private static final WorldArea zmi = new WorldArea(new WorldPoint(3050, 5573, 0), 20, 20);

	@Inject
	private Client client;

	@Inject
	private EasyEmptyConfig config;

	@Override
	protected void startUp()
	{
		log.info("Easy Empty  started!");
	}

	@Override
	protected void shutDown()
	{
		log.info("Easy Empty  stopped!");
	}

	@Subscribe
	public void onClientTick(ClientTick event)
	{
		if (client.getGameState() != GameState.LOGGED_IN || client.isMenuOpen() || client.isKeyPressed(KeyCode.KC_SHIFT))
		{
			return;
		}

		boolean atAltar = false;
		WorldPoint playerLoc = Objects.requireNonNull(client.getLocalPlayer()).getWorldLocation();

		if (zmi.contains2D(playerLoc)) {
			atAltar = true;
		}
		else {
			for (int altarRegion : altars) {
				if (altarRegion == playerLoc.getRegionID()) {
					atAltar = true;
					break;
				}
			}
		}

		if (atAltar) {
			MenuEntry[] menuEntries = client.getMenuEntries();
			int emptyIdx = -1;
			int topIdx = menuEntries.length - 1;
			for (int i = 0; i < topIdx; i++) {

				if (Text.removeTags(menuEntries[i].getOption()).equals("Empty")) {
					emptyIdx = i;
					break;
				}
			}
			if (emptyIdx == -1) {
				return;
			}

			MenuEntry entry1 = menuEntries[emptyIdx];
			MenuEntry entry2 = menuEntries[topIdx];

			menuEntries[emptyIdx] = entry2;
			menuEntries[topIdx] = entry1;

			client.setMenuEntries(menuEntries);
		}
	}

	@Provides
	EasyEmptyConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(EasyEmptyConfig.class);
	}
}
