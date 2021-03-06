package cofh.lib.gui.element.tab;

import cofh.api.tileentity.IReconfigurableFacing;
import cofh.api.tileentity.IReconfigurableSides;
import cofh.api.tileentity.ISidedTexture;
import cofh.lib.gui.GuiBase;
import cofh.lib.gui.GuiProps;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.StringHelper;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import net.minecraft.util.SoundCategory;
import org.lwjgl.opengl.GL11;

public class TabConfiguration extends TabBase {

	public static boolean enable;
	public static int defaultSide = 1;
	public static int defaultHeaderColor = 0xe1c92f;
	public static int defaultSubHeaderColor = 0xaaafb8;
	public static int defaultTextColor = 0x000000;
	public static int defaultBackgroundColor = 0x226688;

	public static final ResourceLocation TAB_ICON = new ResourceLocation(GuiProps.PATH_ICONS + "icon_config.png");

	IReconfigurableFacing myTile;
	IReconfigurableSides myTileSides;

	ISidedTexture myTileTexture;

	public TabConfiguration(GuiBase gui, IReconfigurableFacing theTile) {

		this(gui, defaultSide, theTile);
	}

	public TabConfiguration(GuiBase gui, int side, IReconfigurableFacing theTile) {

		super(gui, side);

		headerColor = defaultHeaderColor;
		subheaderColor = defaultSubHeaderColor;
		textColor = defaultTextColor;
		backgroundColor = defaultBackgroundColor;

		maxHeight = 92;
		maxWidth = 100;
		myTile = theTile;
		myTileSides = (IReconfigurableSides) theTile;
		myTileTexture = (ISidedTexture) theTile;
	}

	@Override
	public void addTooltip(List<String> list) {

		if (!isFullyOpened()) {
			list.add(StringHelper.localize("info.cofh.configuration"));
			return;
		}
	}

	@Override
	public boolean onMousePressed(int mouseX, int mouseY, int mouseButton) {

		if (!isFullyOpened()) {
			return false;
		}
		if (side == LEFT) {
			mouseX += currentWidth;
		}
		mouseX -= currentShiftX;
		mouseY -= currentShiftY;

		if (mouseX < 16 || mouseX >= 80 || mouseY < 20 || mouseY >= 84) {
			return false;
		}
		if (40 <= mouseX && mouseX < 56 && 24 <= mouseY && mouseY < 40) {
			handleSideChange(BlockHelper.ENUM_SIDE_ABOVE[myTile.getFacing()], mouseButton);
		} else if (20 <= mouseX && mouseX < 36 && 44 <= mouseY && mouseY < 60) {
			handleSideChange(BlockHelper.ENUM_SIDE_LEFT[myTile.getFacing()], mouseButton);
		} else if (40 <= mouseX && mouseX < 56 && 44 <= mouseY && mouseY < 60) {
			handleSideChange(EnumFacing.VALUES[myTile.getFacing()], mouseButton);
		} else if (60 <= mouseX && mouseX < 76 && 44 <= mouseY && mouseY < 60) {
			handleSideChange(BlockHelper.ENUM_SIDE_RIGHT[myTile.getFacing()], mouseButton);
		} else if (40 <= mouseX && mouseX < 56 && 64 <= mouseY && mouseY < 80) {
			handleSideChange(BlockHelper.ENUM_SIDE_BELOW[myTile.getFacing()], mouseButton);
		} else if (60 <= mouseX && mouseX < 76 && 64 <= mouseY && mouseY < 80) {
			handleSideChange(BlockHelper.ENUM_SIDE_OPPOSITE[myTile.getFacing()], mouseButton);
		}
		return true;
	}

	@Override
	protected void drawTabBackground() {

		super.drawTabBackground();

		if (!isFullyOpened()) {
			return;
		}
		float colorR = (backgroundColor >> 16 & 255) / 255.0F * 0.6F;
		float colorG = (backgroundColor >> 8 & 255) / 255.0F * 0.6F;
		float colorB = (backgroundColor & 255) / 255.0F * 0.6F;
		GL11.glColor4f(colorR, colorG, colorB, 1.0F);
		gui.drawTexturedModalRect(16, 20, 16, 20, 64, 64);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		gui.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		for (int i = 0; i < 2; i++) {
			gui.drawTextureMapIcon(myTileTexture.getTexture(BlockHelper.ENUM_SIDE_ABOVE[myTile.getFacing()], i), 40, 24);
			gui.drawTextureMapIcon(myTileTexture.getTexture(BlockHelper.ENUM_SIDE_LEFT[myTile.getFacing()], i), 20, 44);
			gui.drawTextureMapIcon(myTileTexture.getTexture(EnumFacing.VALUES[myTile.getFacing()], i), 40, 44);
			gui.drawTextureMapIcon(myTileTexture.getTexture(BlockHelper.ENUM_SIDE_RIGHT[myTile.getFacing()], i), 60, 44);
			gui.drawTextureMapIcon(myTileTexture.getTexture(BlockHelper.ENUM_SIDE_BELOW[myTile.getFacing()], i), 40, 64);
			gui.drawTextureMapIcon(myTileTexture.getTexture(BlockHelper.ENUM_SIDE_OPPOSITE[myTile.getFacing()], i), 60, 64);
		}

	}

	@Override
	protected void drawTabForeground() {

		drawTabIcon(TAB_ICON);
		if (!isFullyOpened()) {
			return;
		}
		getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.configuration"), sideOffset() + 18, 6, headerColor);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	void handleSideChange(EnumFacing side, int mouseButton) {

		if (GuiScreen.isShiftKeyDown()) {
			if (side == EnumFacing.VALUES[myTile.getFacing()]) {
				if (myTileSides.resetSides()) {
					GuiBase.playSound(SoundEvents.UI_BUTTON_CLICK, 0.2F);
				}
			} else if (myTileSides.setSide(side, 0)) {
				GuiBase.playSound(SoundEvents.UI_BUTTON_CLICK, 0.4F);
			}
			return;
		}
		if (mouseButton == 0) {
			if (myTileSides.incrSide(side)) {
				GuiBase.playSound(SoundEvents.UI_BUTTON_CLICK, 0.8F);
			}
		} else if (mouseButton == 1) {
			if (myTileSides.decrSide(side)) {
				GuiBase.playSound(SoundEvents.UI_BUTTON_CLICK, 0.6F);
			}
		}
	}

}
