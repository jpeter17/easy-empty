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

@ConfigGroup("easyempty")
public interface EasyEmptyConfig extends Config
{
    @ConfigItem(keyName = "emptyPouches", name = "Empty pouches near altar", description = "Left-click empty pouches near altar", position = 1)
    default boolean emptyPouches()
    {
        return true;
    }

    @ConfigItem(keyName = "bankFill", name = "Fill pouches from bank", description = "Left-click fill pouches from bank menu", position = 2)
    default boolean bankFill()
    {
        return true;
    }

    @ConfigItem(keyName = "swapStam", name = "Stamina Potion(1) swaps", description = "Left-click drink/withdraw-1 from bank menu", position = 3)
    default boolean swapStam()
    {
        return true;
    }

    @ConfigItem(keyName = "swapNeck", name = "Binding Necklace swaps", description = "Left-click wear/withdraw-1 from bank menu", position = 4)
    default boolean swapNeck()
    {
        return true;
    }
    @ConfigItem(keyName = "swapTali", name = "Talisman swaps", description = "withdraw-2/3 from bank menu", position = 5)
    default boolean swapTali()
    {
        return true;
    }
    
}
