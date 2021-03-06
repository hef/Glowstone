package net.glowstone.msg.handler;

import net.glowstone.block.BlockProperties;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.inventory.GlowItemStack;
import net.glowstone.item.ItemProperties;
import net.glowstone.msg.BlockChangeMessage;
import org.bukkit.GameMode;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;

import net.glowstone.EventFactory;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.BlockPlacementMessage;
import net.glowstone.net.Session;
import org.bukkit.material.MaterialData;

/**
 * A {@link MessageHandler} which processes digging messages.
 */
public final class BlockPlacementMessageHandler extends MessageHandler<BlockPlacementMessage> {

    @Override
    public void handle(Session session, GlowPlayer player, BlockPlacementMessage message) {
        if (player == null)
            return;

        /**
         * The notch client's packet sending is weird. Here's how it works:
         * If the client is clicking a block not in range, sends a packet with x=-1,y=255,z=-1
         * If the client is clicking a block in range with an item in hand (id > 255)
         * Sends both the normal block placement packet and a (-1,255,-1) one
         * If the client is placing a block in range with a block in hand, only one normal packet is sent
         * That is how it usually happens. Sometimes it doesn't happen like that.
         * Therefore, a hacky workaround.
         */
        if (message.getDirection() == 255) {
            // Right-clicked air. Note that the client doesn't send this if they are holding nothing.
            BlockPlacementMessage previous = session.getPreviousPlacement();
            if (previous == null
                    || previous.getCount() != message.getCount()
                    && previous.getId() != message.getId()
                    && previous.getDamage() != message.getDamage()) {
                EventFactory.onPlayerInteract(player, Action.RIGHT_CLICK_AIR);
            }
            session.setPreviousPlacement(null);
            return;
        }
        session.setPreviousPlacement(message);

        GlowBlock against = player.getWorld().getBlockAt(message.getX(), message.getY(), message.getZ());
        BlockFace face = MessageHandlerUtils.messageToBlockFace(message.getDirection());
        if (face == BlockFace.SELF) return;


        GlowBlock target = against.getRelative(face);
        GlowItemStack holding = player.getItemInHand();
        boolean sendRevert = false;
        PlayerInteractEvent interactEvent = EventFactory.onPlayerInteract(player, Action.RIGHT_CLICK_BLOCK, against, face);
        if (interactEvent.useInteractedBlock() != Event.Result.DENY) {
            if (!BlockProperties.get(against.getTypeId()).getPhysics().interact(player, against, true, face)) {
                sendRevert = true;
            }
        }
        if (interactEvent.isCancelled()) {
            sendRevert = true;
        }
        if (holding != null) {
            if (interactEvent.useItemInHand() != Event.Result.DENY) {
                if (holding.getTypeId() > 255 && !ItemProperties.get(holding.getTypeId()).getPhysics().interact(player, against, holding, Action.RIGHT_CLICK_BLOCK, face)) {
                    sendRevert = true;
                }
            }
            MaterialData placedId = new MaterialData(holding.getTypeId(), (byte) holding.getDurability());
            if (placedId.getItemTypeId() > 255)
                placedId = ItemProperties.get(placedId.getItemTypeId()).getPhysics().getPlacedBlock(face, holding.getDurability());
            if (placedId.getItemTypeId() == -1) sendRevert = true;
            if (!sendRevert && placedId.getItemTypeId() < 256) {
                if (target.isEmpty() || target.isLiquid()) {
                    if (EventFactory.onBlockCanBuild(target, placedId.getItemTypeId(), face).isBuildable()) {
                        GlowBlockState newState = BlockProperties.get(placedId.getItemTypeId()).getPhysics().placeAgainst(player, target.getState(), placedId, face);
                        BlockPlaceEvent event = EventFactory.onBlockPlace(target, newState, against, player);

                        if (!event.isCancelled() && event.canBuild()) {
                            newState.update(true);
                            if (newState.getX() != target.getX() || newState.getY() != target.getY() || newState.getZ() != target.getZ()) {
                                sendRevert = true;
                            }

                            if (player.getGameMode() != GameMode.CREATIVE) {
                                holding.setAmount(holding.getAmount() - 1);
                                if (holding.getAmount() == 0) {
                                    player.setItemInHand(null);
                                } else {
                                    player.setItemInHand(holding);
                                }
                            }
                        } else {
                            sendRevert = true;
                        }
                    } else {
                        sendRevert = true;
                    }
                }
            }
        }
        if (sendRevert) {
            player.getSession().send(new BlockChangeMessage(target.getX(), target.getY(), target.getZ(), target.getTypeId(), target.getData()));
            player.setItemInHand(holding);
        }
    }

}
