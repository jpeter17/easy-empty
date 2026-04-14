/*
 * Copyright (c) 2022, Jacob Petersen <jakepetersen1221@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.easyEmpty;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.PostMenuSort;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.ArrayUtils;

import javax.inject.Inject;

@Slf4j
@PluginDescriptor(
	name = "Runecrafting Utilities",
	description = "Provides various menu swaps to improve runecrafting",
	tags = {"swap","swapper","menu","entry","menu entry swapper","runecrafting","pouch","essence","easy","empty"}
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

	int[] pouches = {
			ItemID.SMALL_POUCH,
			ItemID.MEDIUM_POUCH,
			ItemID.LARGE_POUCH,
			ItemID.GIANT_POUCH,
			ItemID.COLOSSAL_POUCH,
			ItemID.MEDIUM_POUCH_5511,
			ItemID.LARGE_POUCH_5513,
			ItemID.GIANT_POUCH_5515,
			ItemID.COLOSSAL_POUCH_26786
	};

	private static final WorldArea zmi = new WorldArea(new WorldPoint(3050, 5573, 0), 20, 20);

	boolean bankFill, swapStam, swapNeck, emptyPouches, fillPouches;
	EasyEmptyConfig.ShiftOption emptyPouchesShift, fillPouchesShift;

	@Inject
	private Client client;

	@Inject
	private EasyEmptyConfig config;

	@Override
	protected void startUp()
	{
		bankFill = config.bankFill();
		swapStam = config.swapStam();
		swapNeck = config.swapNeck();
		emptyPouches = config.emptyPouches();
		emptyPouchesShift = config.emptyPouchesShift();
		fillPouches = config.fillPouches();
		fillPouchesShift = config.fillPouchesShift();
		log.info("Easy Empty  started!");
	}

	@Override
	protected void shutDown()
	{
		log.info("Easy Empty  stopped!");
	}
	// Run after built-in menu swaps
	@Subscribe(priority=-1)
	public void onClientTick(ClientTick event) {
		if (client.getGameState() != GameState.LOGGED_IN) {
			return;
		}
		updateBank();
		// We call this during game ticks also to avoid flickering from menu entry swapper
		// Specifically:
		// 1) At an altar
		// 2) Mouse hovering over a pouch
		// 3) Essence in inventory
		// 4) Shift-click configured for Fill or Empty
		// Pressing shift causes a staggered update
		updatePouch();
	}

	// Run after built-in menu swaps, so we can detect shift-click swaps
	@Subscribe(priority=-1)
	public void onPostMenuSort(PostMenuSort event)
	{
		updatePouch();
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{

		if (event.getGroup().equals("easyempty")) {
			switch (event.getKey()) {
				case "bankFill":			bankFill = config.bankFill();
					break;
				case "swapNeck":			swapNeck = config.swapNeck();
					break;
				case "swapStam":			swapStam = config.swapStam();
					break;
				case "emptyPouches":		emptyPouches = config.emptyPouches();
					break;
				case "emptyPouchesShift": 	emptyPouchesShift = config.emptyPouchesShift();
					break;
				case "fillPouches": 		fillPouches = config.fillPouches();
					break;
				case "fillPouchesShift": 	fillPouchesShift = config.fillPouchesShift();
					break;
			}
		}
	}

	protected void updateBank() {
		if (client.isMenuOpen() || client.isKeyPressed(KeyCode.KC_SHIFT)
				|| client.getWidget(WidgetInfo.BANK_CONTAINER) == null) {
			return;
		}
		MenuEntry[] menuEntries = client.getMenuEntries();
		for (int i = menuEntries.length - 1; i >= 0; i--) {
			Widget widget = menuEntries[i].getWidget();
			MenuAction entryType = menuEntries[i].getType();
			String entryOption = menuEntries[i].getOption();

			if (widget != null && (entryType == MenuAction.CC_OP_LOW_PRIORITY || entryType == MenuAction.CC_OP) &&
					((bankFill && entryOption.startsWith("Fill") && ArrayUtils.contains(pouches, widget.getItemId())) ||
							(swapStam && entryOption.matches("Drink|Withdraw-1") && widget.getItemId() == ItemID.STAMINA_POTION1) ||
							(swapNeck && entryOption.matches("Wear|Withdraw-1") && widget.getItemId() == ItemID.BINDING_NECKLACE))) {
				MenuEntry entry = menuEntries[i];

				entry.setType(MenuAction.CC_OP);
				menuEntries[i] = menuEntries[menuEntries.length - 1];
				menuEntries[menuEntries.length - 1] = entry;

				client.setMenuEntries(menuEntries);
				break;
			}
		}
	}

	protected boolean atAltar()
	{
		Player player = client.getLocalPlayer();
		if (player == null) return false;
		WorldPoint playerLoc = player.getWorldLocation();

		for (int altarRegion : altars) {
			if (altarRegion == playerLoc.getRegionID()) {
				return true;
			}
		}

		return zmi.contains2D(playerLoc);
	}

	protected void updatePouch()
	{
		if (atAltar()) {
			if (emptyPouches) {
				updatePouch("Empty", emptyPouchesShift);
			}
		} else {
			if(fillPouches) {
				updatePouch("Fill", fillPouchesShift);
			}
		}
	}

	protected String flipFillEmpty(String currentLeftClick)
	{
		if (currentLeftClick.equals("Empty")) return "Fill";
		if (currentLeftClick.equals("Fill")) return "Empty";
		// Leave other values unmodified
		return currentLeftClick;
	}

	@SuppressWarnings("ReassignedVariable")
    protected void updatePouch(String desiredLeftClick, EasyEmptyConfig.ShiftOption shiftOption)
	{
		if (client.isMenuOpen()) {
			return;
		}

		boolean doInvert = false;
		if (client.isKeyPressed(KeyCode.KC_SHIFT)) {
			if (shiftOption == EasyEmptyConfig.ShiftOption.UNMODIFIED) {
				return;
			}
			if (shiftOption == EasyEmptyConfig.ShiftOption.FILL) {
				desiredLeftClick = "Fill";
			}
			if (shiftOption == EasyEmptyConfig.ShiftOption.EMPTY) {
				desiredLeftClick = "Empty";
			}
			if (shiftOption == EasyEmptyConfig.ShiftOption.DYNAMIC) {
				// If the current option is Fill or Empty, then we flip it.
				// Otherwise, keep it the same
				doInvert = true;
			}
		}

		MenuEntry[] menuEntries = client.getMenuEntries();
		int topIdx = menuEntries.length - 1;
		if (!ArrayUtils.contains(pouches, menuEntries[topIdx].getItemId())) {
			return;
		}

		if (doInvert) {
			String currentLeftClick = Text.removeTags(menuEntries[topIdx].getOption());

			desiredLeftClick = flipFillEmpty(currentLeftClick);
			if (desiredLeftClick.equals(currentLeftClick)) return;
		}

		setLeftClick(menuEntries, desiredLeftClick);
	}

	// Takes in menuEntries to avoid multiple get calls
	protected void setLeftClick(MenuEntry[] menuEntries, String desiredLeftClick)
	{
		final int topIdx = menuEntries.length - 1;
		int originalIdx = -1;
		for (int i = 0; i < topIdx; i++) {
			if (Text.removeTags(menuEntries[i].getOption()).equals(desiredLeftClick)) {
				originalIdx = i;
				break;
			}
		}
		if (originalIdx == -1) {
			// Option is already on top
			return;
		}

		MenuEntry entry1 = menuEntries[originalIdx];
		MenuEntry entry2 = menuEntries[topIdx];

		menuEntries[originalIdx] = entry2;
		menuEntries[topIdx] = entry1;
		// The swap is done correctly, but the top entry isn't left-clickable
		menuEntries[originalIdx].setType(MenuAction.CC_OP);
		menuEntries[topIdx].setType(MenuAction.CC_OP);
		client.setMenuEntries(menuEntries);
	}

	@Provides
	EasyEmptyConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(EasyEmptyConfig.class);
	}
}
