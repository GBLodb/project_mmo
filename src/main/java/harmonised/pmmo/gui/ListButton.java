package harmonised.pmmo.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import harmonised.pmmo.config.JType;
import harmonised.pmmo.skills.Skill;
import harmonised.pmmo.util.XP;
import harmonised.pmmo.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.Pose;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class ListButton extends Button
{
    private final ResourceLocation items = XP.getResLoc( Reference.MOD_ID, "textures/gui/items.png" );
    private final ResourceLocation buttons = XP.getResLoc( Reference.MOD_ID, "textures/gui/buttons.png" );
//    private final Screen screen = new SkillsScreen( new TextComponentTranslation( "pmmo.potato" ));
    public int elementOne, elementTwo;
    public int offsetOne, offsetTwo;
    public double mobWidth, mobHeight, mobScale;
    public boolean unlocked = true;
    public ItemStack itemStack;
    public String regKey, title, buttonText;
    public List<String> text = new ArrayList<>();
    public List<String> tooltipText = new ArrayList<>();
    Entity testEntity = null;
    EntityLiving entity = null;
    ItemRenderer itemRenderer = Minecraft.getMinecraft().getItemRenderer();

    public ListButton( int posX, int posY, int elementOne, int elementTwo, String regKey, JType jType, String buttonText, IPressable onPress )
    {
        super(posX, posY, 32, 32, "", onPress);
        this.regKey = regKey;
        this.buttonText = buttonText;
        this.itemStack = new ItemStack( XP.getItem( regKey ) );
        this.elementOne = elementOne * 32;
        this.elementTwo = elementTwo * 32;

        if( ForgeRegistries.ENTITIES.containsKey( XP.getResLoc( regKey ) ) )
            testEntity = ForgeRegistries.ENTITIES.getValue( XP.getResLoc( regKey ) ).create( Minecraft.getMinecraft().world );

        if( testEntity instanceof EntityLiving )
            entity = (EntityLiving) testEntity;

        switch( jType )
        {
            case FISH_ENCHANT_POOL:
                this.title = new TextComponentTranslation( ForgeRegistries.ENCHANTMENTS.getValue( XP.getResLoc( regKey ) ).getDisplayName( 1 ).getUnformattedText().replace( " I", "" ) ).getUnformattedText();
                break;

            case XP_VALUE_BREED:
            case XP_VALUE_TAME:
            case REQ_KILL:
                this.title = new TextComponentTranslation( ForgeRegistries.ENTITIES.getValue( XP.getResLoc( regKey ) ).getTranslationKey() ).getUnformattedText();
                break;

            case DIMENSION:
                if( regKey.equals( "all_dimensions" ) )
                    this.title = new TextComponentTranslation( "pmmo.allDimensions" ).getFormattedText();
                else if( regKey.equals( "minecraft:overworld" ) || regKey.equals( "minecraft:the_nether" ) || regKey.equals( "minecraft:the_end" ) )
                    this.title = new TextComponentTranslation( regKey ).getFormattedText();
                else if( ForgeRegistries.MOD_DIMENSIONS.containsKey( XP.getResLoc( regKey ) ) )
                    this.title = new TextComponentTranslation( ForgeRegistries.MOD_DIMENSIONS.getValue( XP.getResLoc( regKey ) ).getRegistryName().toString() ).getFormattedText();
                break;

            case STATS:
                this.title = new TextComponentTranslation( "pmmo." + regKey ).setStyle( XP.getSkillStyle(Skill.getSkill( regKey ) ) ).getFormattedText();
                break;

            case REQ_BIOME:
//                this.title = new TextComponentTranslation( ForgeRegistries.BIOMES.getValue( XP.getResLoc( regKey ) ).getTranslationKey() ).getUnformattedText();
                this.title = new TextComponentTranslation( regKey ).getUnformattedText();
                break;

            default:
                this.title = new TextComponentTranslation( itemStack.getTranslationKey() ).getUnformattedText();
                break;
        }

        switch( regKey )
        {
            case "pmmo.otherCrafts":
            case "pmmo.otherAnimals":
            case "pmmo.otherPassiveMobs":
            case "pmmo.otherAggresiveMobs":
                this.title = new TextComponentTranslation( new TextComponentTranslation( regKey ).getFormattedText() ).getUnformattedText();
                break;
        }

        if( elementOne > 23 )
            offsetOne = 192;
        else if( elementOne > 15 )
            offsetOne = 128;
        else if( elementOne > 7 )
            offsetOne = 64;
        else
            offsetOne = 0;

        if( elementTwo > 23 )
            offsetTwo = 192;
        else if( elementTwo > 15 )
            offsetTwo = 128;
        else if( elementTwo > 7 )
            offsetTwo = 64;
        else
            offsetTwo = 0;
    }

    @Override
    public int getHeight()
    {
        int height = 11;

        for( String a : text )
        {
            height += 9;
        }

        if( height > 32 )
            return height;
        else
            return 32;
    }

    public void clickActionGlossary()
    {
//        LOGGER.info( "Clicked " + this.title + " Button" );
        GlossaryScreen.setButtonsToKey( regKey );
        Minecraft.getMinecraft().displayGuiScreen( new GlossaryScreen( Minecraft.getMinecraft().player.getUniqueID(), new TextComponentTranslation( "pmmo.glossary" ), false ) );
    }

    public void clickActionSkills()
    {
        if( !Skill.getSkill( regKey ).equals( Skill.INVALID_SKILL ) )
            Minecraft.getMinecraft().displayGuiScreen( new ListScreen( Minecraft.getMinecraft().player.getUniqueID(), new TextComponentTranslation( "" ), regKey, JType.HISCORE, Minecraft.getMinecraft().player ) );
    }

    @Override
    public void renderButton(int x, int y, float partialTicks)
    {
        Minecraft minecraft = Minecraft.getMinecraft();
        FontRenderer fontrenderer = minecraft.fontRenderer;
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage(this.isHovered());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        minecraft.getTextureManager().bindTexture( buttons );
        this.blit(this.x, this.y, this.offsetOne + ( this.isHovered() ? 32 : 0 ), this.elementOne, this.width, this.height);
        minecraft.getTextureManager().bindTexture( items );
        this.blit(this.x, this.y, this.offsetTwo + ( this.isHovered() ? 32 : 0 ), this.elementTwo, this.width, this.height);
        if( !itemStack.getItem().equals( Items.AIR ) && entity == null )
            itemRenderer.renderItemIntoGUI( itemStack, this.x + 8, this.y + 8 );

        if( entity != null )
        {
            mobHeight = entity.getSize( Pose.STANDING ).height;
            mobWidth = entity.getSize( Pose.STANDING ).width;
            mobScale = 27;

            if( mobHeight > 0 )
                mobScale /= Math.max(mobHeight, mobWidth);

            drawEntityOnScreen( this.x + this.width / 2, this.y + this.height - 2, (int) mobScale, entity );
        }

        this.renderBg(minecraft, x, y);
        int j = getFGColor();
        this.drawCenteredString(fontrenderer, this.buttonText, this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
    }

    public static void drawEntityOnScreen(int posX, int posY, int scale, EntityLiving p_228187_5_)
    {
        float f = (float) ( (System.currentTimeMillis() / 25D ) % 360);
        float f1 = 0;
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float)posX, (float)posY, 1050.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        MatrixStack matrixstack = new MatrixStack();
        matrixstack.translate(0.0D, 0.0D, 1000.0D);
        matrixstack.scale((float)scale, (float)scale, (float)scale);
        Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
        quaternion.multiply(quaternion1);
        matrixstack.rotate(quaternion);
        float f2 = p_228187_5_.renderYawOffset;
        float f3 = p_228187_5_.rotationYaw;
        float f4 = p_228187_5_.rotationPitch;
        float f5 = p_228187_5_.prevRotationYawHead;
        float f6 = p_228187_5_.rotationYawHead;
        p_228187_5_.renderYawOffset = f;
        p_228187_5_.rotationYaw = f;
        p_228187_5_.rotationPitch = -f1 * 20.0F;
        p_228187_5_.rotationYawHead = f;
        p_228187_5_.prevRotationYawHead = 0;
        EntityRendererManager entityrenderermanager = Minecraft.getMinecraft().getRenderManager();
        quaternion1.conjugate();
        entityrenderermanager.setCameraOrientation(quaternion1);
        entityrenderermanager.setRenderShadow(false);
        IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getMinecraft().getRenderTypeBuffers().getBufferSource();
        entityrenderermanager.renderEntityStatic(p_228187_5_, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixstack, irendertypebuffer$impl, 15728880);
        irendertypebuffer$impl.finish();
        entityrenderermanager.setRenderShadow(true);
        p_228187_5_.renderYawOffset = f2;
        p_228187_5_.rotationYaw = f3;
        p_228187_5_.rotationPitch = f4;
        p_228187_5_.prevRotationYawHead = f5;
        p_228187_5_.rotationYawHead = f6;
        RenderSystem.popMatrix();
    }
}
