package chbachman.armour.gui.recipe;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import chbachman.api.IUpgrade;
import chbachman.armour.crafting.Recipe;
import chbachman.armour.gui.GuiHelper;
import chbachman.armour.network.ArmourPacket;
import chbachman.armour.network.ArmourPacket.PacketTypes;
import chbachman.armour.reference.ResourceLocationHelper;
import cofh.core.gui.GuiBaseAdv;
import cofh.core.network.PacketHandler;
import cofh.lib.gui.element.ElementButton;

public class ArmourGuiRecipe extends GuiBaseAdv {
    
    private static final ResourceLocation TEXTURE = ResourceLocationHelper.getResourceLocation("/gui/recipeGui.png");
    
    public ArmourContainerRecipe container;
    public ElementButton rightArrow;
    public ElementButton leftArrow;
    public TabCompatible compatible;
    
    
    public ArmourGuiRecipe(ArmourContainerRecipe container, InventoryPlayer inventory) {
        super(container, TEXTURE);
        
        this.container = container;
        
        this.texture = TEXTURE;
        this.drawTitle = false;
        this.drawInventory = false;
        this.xSize = 176;
        this.ySize = 152;
        
        
        leftArrow = new ElementButton(this, 5, 5, "Go Back", 227, 12, 227, 12, 227, 12, 7, 7, TEXTURE.toString());
        rightArrow = new ElementButton(this, 164, 5, "Next", 235, 12, 235, 12, 235, 12, 7, 7, TEXTURE.toString());
        
        compatible = new TabCompatible(this);
        
        rightArrow.setToolTip("Next");
        leftArrow.setToolTip("Back");
    }
    
    @Override
    public void initGui() {
        super.initGui();
        
        this.addTab(compatible);
        this.addElement(leftArrow);
        this.addElement(rightArrow);
        
        for (int i = 9; i < 9 + ArmourContainerRecipe.modularItems.length; i++) {
            ((Slot) this.container.inventorySlots.get(i)).xDisplayPosition = -this.guiLeft - 16;
            ((Slot) this.container.inventorySlots.get(i)).yDisplayPosition = -this.guiTop - 16;
        }
    }
    
    
    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
    	super.drawGuiContainerForegroundLayer(x, y);
    	
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int x, int y) {
        
    	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		bindTexture(texture);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		mouseX = x - guiLeft;
		mouseY = y - guiTop;

		GL11.glPushMatrix();
		GL11.glTranslatef(guiLeft, guiTop, 0.0F);
		drawElements(partialTick, false);
		drawTabs(partialTick, false);
		GL11.glPopMatrix();
        
		if(this.container.recipe != null){

    		IUpgrade upgrade = this.container.recipe.getRecipeOutput();

    		GuiHelper.drawStringBounded(this, upgrade.getName(), 70, this.guiLeft + 100, this.guiTop + 18, 0xFFFFFF);

    		GuiHelper.drawStringBounded(this, upgrade.getInformation(), 159, this.guiLeft + 11, this.guiTop + 80, 0xFFFFFF);
    	}
    }
    
    @Override
    public void handleElementButtonClick(String buttonName, int mouseButton){
        super.handleElementButtonClick(buttonName, mouseButton);
        
        if(buttonName.equals("Go Back")){
            this.container.index--;
            try{
                this.container.recipe = Recipe.craftingList.get(this.container.index);
            }catch(IndexOutOfBoundsException e){
                this.container.index = Recipe.craftingList.size() - 1;
                this.container.recipe = Recipe.craftingList.get(this.container.index);
            }
        }else if(buttonName.equals("Next")){
            this.container.index++;
            this.container.recipe = Recipe.craftingList.get(this.container.index % Recipe.craftingList.size());
            
        }
        
        PacketHandler.sendToServer(ArmourPacket.getPacket(PacketTypes.BUTTON).addString(buttonName).addInt(this.container.index));
    }
    
    @Override
    public void keyTyped(char characterTyped, int keyPressed){
        if (keyPressed == Keyboard.KEY_ESCAPE || characterTyped == this.mc.gameSettings.keyBindInventory.getKeyCode())
        {
            PacketHandler.sendToServer(ArmourPacket.getPacket(ArmourPacket.PacketTypes.KEYTYPED).addShort((short) characterTyped).addInt(keyPressed));
            return;
        }
        super.keyTyped(characterTyped, keyPressed);
    }
    
    
}
