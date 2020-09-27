package com.windanesz.ancientspellcraft.client.gui;

import com.windanesz.ancientspellcraft.packet.PacketControlInput;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.tileentity.TileCrystalBallCognizance;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.tileentity.SlotItemList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class ContainerCrystalBallCognizance extends Container {

	//	private int cookTime;
	//	private int totalCookTime;
	//	private int furnaceBurnTime;
	//	private int currentItemBurnTime;

	// These store cache values, used by the server to only update the client side tile entity when values have changed

	private int [] cachedFields;

	private int researchDuration;    // not saved to nbt
	private int researchProgress;
	public int currentHintTypeId = 0;
	public int currentHintId = 0;


	public static int CRYSTAL_SLOT = 0;
	public static int BOOK_SLOT = 1;

	public static List<String> HINT_TYPES = Arrays.asList("none", "failed", "discovered", "heal_ally", "fire", "earth", "ice", "necromancy", "healing", "lightning", "sorcery", "ancient", "buff", "attack", "projectile",
			"defense", "utility", "construct", "minion", "alteration", "pocket_furnace", "arcane_lock", "remove_curse", "resurrection");

	//	if ((HINT_TYPES.contains("utility"))) {
	//		HINT_TYPES.indexOf("utility")}
	//

	public static final LinkedHashMap<String, Integer> HINTS_COUNT = new LinkedHashMap<String, Integer>() {
		{
			put("none", 1);
			put("failed", 9);
			put("discovered", 10);
			put("fire", 6);
			put("earth", 5);
			put("ice", 6);
			put("necromancy", 5);
			put("healing", 4);
			put("lightning", 4);
			put("sorcery", 5);
			put("ancient", 7);
			put("buff", 4);
			put("attack", 4);
			put("projectile", 4);
			put("defense", 4);
			put("utility", 5);
			put("construct", 4);
			put("minion", 4);
			put("alteration", 4);
			put("heal_ally", 4);
			put("pocket_furnace", 4);
			put("arcane_lock", 4);
			put("remove_curse", 4);
			put("resurrection", 5);
		}
	};

	//	public static final ResourceLocation EMPTY_SLOT_CRYSTAL = new ResourceLocation(Wizardry.MODID, "gui/empty_slot_crystal");
	//
	//	public TileCrystalBallCognizance tileCrystalBallCognizance;
	//
	//	public ContainerCrystalBallCognizance(IInventory inventory, TileCrystalBallCognizance tileentity) {
	//		this.tileCrystalBallCognizance = tileentity;
	//
	//		this.addSlotToContainer(new SlotItemList(tileentity, 0, 13, 101, 64,
	//				WizardryItems.magic_crystal, WizardryItems.crystal_shard, WizardryItems.grand_crystal))
	//				.setBackgroundName(EMPTY_SLOT_CRYSTAL.toString());
	//	}
	//

	private TileCrystalBallCognizance tileCrystalBall;

	public ContainerCrystalBallCognizance(EntityPlayer player, IInventory playerInv, TileCrystalBallCognizance te) {
		this.tileCrystalBall = te;
		te.openInventory(player);

		// Tile Entity, Slot 0-8, Slot IDs 0-8
		//		for (int y = 0; y < 3; ++y) {
		//			for (int x = 0; x < 3; ++x) {
		// crystal slot
		//		this.addSlotToContainer(new Slot(te, 0, 14, 100 + 26));
		this.addSlotToContainer(new SlotItemList(te, 0, 14, 100 + 26, 64, WizardryItems.magic_crystal, WizardryItems.crystal_shard, WizardryItems.grand_crystal));

		/// book
		this.addSlotToContainer(new SlotItemList(te, 1, 62 + 17, 100 + 15, 1, WizardryItems.spell_book, WizardryItems.scroll, AncientSpellcraftItems.ancient_spellcraft_spell_book, AncientSpellcraftItems.ancient_spellcraft_scroll));
		//			}
		//		}

		int n = 75;

		// Player Inventory, Slot 9-35, Slot IDs 9-35
		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 9; ++x) {
				this.addSlotToContainer(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, n + 84 + y * 18));
			}
		}

		// Player Inventory, Slot 0-8, Slot IDs 36-44
		for (int x = 0; x < 9; ++x) {
			this.addSlotToContainer(new Slot(playerInv, x, 8 + x * 18, n + 142));
		}
	}

	/**
	 * Determines whether supplied player can use this container
	 */
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return this.tileCrystalBall.isUsableByPlayer(playerIn);
	}

	/**
	 * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
	 * inventory and the other inventory(s).
	 */
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (index < this.tileCrystalBall.getSizeInventory()) {
				if (!this.mergeItemStack(itemstack1, this.tileCrystalBall.getSizeInventory(), this.inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(itemstack1, 0, this.tileCrystalBall.getSizeInventory(), false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
		}

		return itemstack;
	}

	/**
	 * Looks for changes made in the container, sends them to every listener.
	 */
	@SuppressWarnings("Duplicates")
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		boolean allFieldsHaveChanged = false;
		boolean fieldHasChanged [] = new boolean[tileCrystalBall.getFieldCount()];
		if (cachedFields == null) {
			cachedFields = new int[tileCrystalBall.getFieldCount()];
			allFieldsHaveChanged = true;
		}
		for (int i = 0; i < cachedFields.length; ++i) {
			if (allFieldsHaveChanged || cachedFields[i] != tileCrystalBall.getField(i)) {
				cachedFields[i] = tileCrystalBall.getField(i);
				fieldHasChanged[i] = true;
			}
		}

		// go through the list of listeners (players using this container) and update them if necessary
		for (IContainerListener listener : this.listeners) {
			for (int fieldID = 0; fieldID < tileCrystalBall.getFieldCount(); ++fieldID) {
				if (fieldHasChanged[fieldID]) {
					// Note that although sendWindowProperty takes 2 ints on a server these are truncated to shorts
					listener.sendWindowProperty(this, fieldID, cachedFields[fieldID]);
				}
			}
		}
	}
		
		
		
//		super.detectAndSendChanges();
//
//		for (int i = 0; i < this.listeners.size(); ++i) {
//			IContainerListener icontainerlistener = this.listeners.get(i);
//
//			if (this.researchProgress != this.tileCrystalBall.getField(0)) {
//				//				System.out.println("field was updated");
//				icontainerlistener.sendWindowProperty(this, 0, this.tileCrystalBall.getField(0));
//			}
//			if (this.researchDuration != this.tileCrystalBall.getField(1)) {
//				//				System.out.println("field was updated");
//				icontainerlistener.sendWindowProperty(this, 1, this.tileCrystalBall.getField(1));
//			}
//
//			if (this.currentHintTypeId != this.tileCrystalBall.getField(2)) {
//								System.out.println("field tileCrystalBallwas updated");
//				icontainerlistener.sendWindowProperty(this, 2, this.tileCrystalBall.getField(2));
//			}
//
//			if (this.currentHintId != this.tileCrystalBall.getField(3)) {
//								System.out.println("field tileCrystalBall was updated");
//				icontainerlistener.sendWindowProperty(this, 3, this.tileCrystalBall.getField(3));
//			}
//			//			if (this.researchButton != this.tileCrystalBall.getField(2)) {
//			//				System.out.println("field researchButton was updated");
//			//				System.out.println("in container researchButton:" + researchButton);
//			//				System.out.println("in tile researchButton:" + this.tileCrystalBall.getField(2));
//			//				this.tileCrystalBall.setField(2, researchButton);
//			//				icontainerlistener.sendWindowProperty(this, 2, this.tileCrystalBall.getField(2));
//			//			}
//		}

		//		this.furnaceBurnTime = this.tileCrystalBall.getField(0);
		////new
//
//		this.researchProgress = this.tileCrystalBall.getField(0);
//		this.researchDuration = this.tileCrystalBall.getField(1);
//		this.currentHintTypeId = this.tileCrystalBall.getField(2);
//		this.currentHintId = this.tileCrystalBall.getField(3);
//
//		System.out.println("currentHintTypeId: " + currentHintTypeId);
//		System.out.println("currentHintId: " + currentHintId);
//		//		this.researchButton = this.tileCrystalBall.getField(2);
//
//		////
//	}

	/**
	 * Called when the container is closed.
	 */
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		this.tileCrystalBall.closeInventory(playerIn);
	}

	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		this.tileCrystalBall.setField(id, data);
	}

	/**
	 * Called (via {@link PacketControlInput PacketControlInput}) when the apply button in
	 * the sphere of cognizance GUI is pressed.
	 */
	// As of 2.1, for the sake of events and neatness of code, this was moved here from TileEntityArcaneWorkbench.
	// As of 4.2, the spell binding/charging/upgrading code was delegated (via IWorkbenchItem) to the items themselves.
	@SuppressWarnings("unused")
	public void onApplyButtonPressed() {
		//
		//		Slot centre = this.getSlot(CENTRE_SLOT);
		//
		//			Slot[] spellBooks = this.inventorySlots.subList(0, 8).toArray(new Slot[8]);
		//		System.out.println("this should happen on the server ....");
		tileCrystalBall.attemptBeginResearch();
		//			if(((IWorkbenchItem)centre.getStack().getItem())
		//					.onApplyButtonPressed(player, centre, this.getSlot(CRYSTAL_SLOT), this.getSlot(UPGRADE_SLOT), spellBooks)){

		//				if(player instanceof EntityPlayerMP){ // TODO advancement maybe
		//					WizardryAdvancementTriggers.arcane_workbench.trigger((EntityPlayerMP)player, centre.getStack());
		//				}
	}
}
