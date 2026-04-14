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

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;


@ConfigGroup("easyempty")
public interface EasyEmptyConfig extends Config
{
    enum ShiftOption {
        UNMODIFIED,
        FILL,
        EMPTY,
        DYNAMIC
    }

    @ConfigItem(keyName = "emptyPouches", name = "Empty pouches near altar",
            description = "Left-click always empties pouches near altar, regardless of essence in inventory", position = 1)
    default boolean emptyPouches()
    {
        return true;
    }

    @ConfigItem(keyName = "bankFill", name = "Fill pouches from bank",
            description = "Left-click fills pouches from bank menu", position = 2)
    default boolean bankFill()
    {
        return true;
    }

    @ConfigItem(keyName = "swapStam", name = "Stamina Potion(1) swaps",
            description = "Left-click drink/withdraw-1 from bank menu", position = 3)
    default boolean swapStam()
    {
        return true;
    }

    @ConfigItem(keyName = "swapNeck", name = "Binding Necklace swaps",
            description = "Left-click wear/withdraw-1 from bank menu", position = 4)
    default boolean swapNeck()
    {
        return true;
    }

    @ConfigSection(
            name = "Experimental",
            description = "Advanced options for modifying essence pouch behavior",
            position = 5
    )
    String experimental = "experimental";

    @ConfigItem(keyName = "emptyPouchesShift", name = "Altar shift-click",
            description = "Configure shift-click near an altar. Requires left-click setting enabled. Dynamic flips Fill/Empty. Recommended: Fill or Dynamic",
            section = experimental, position = 1
    )
    default ShiftOption emptyPouchesShift() { return ShiftOption.UNMODIFIED; }
    @ConfigItem(keyName = "fillPouches",
            name = "Fill pouches away from altar",
            description = "Left-click always fills pouches when not near altar, regardless of essence in inventory",
            section = experimental, position = 2
    )
    default boolean fillPouches() { return true; }

    @ConfigItem(keyName = "fillPouchesShift", name = "Non-altar shift-click",
            description = "Configure shift-click when not near an altar. Requires left-click setting enabled. Dynamic flips Fill/Empty. Recommended: Empty or Dynamic",
            section = experimental, position = 3
    )
    default ShiftOption fillPouchesShift() { return ShiftOption.UNMODIFIED; }
}
