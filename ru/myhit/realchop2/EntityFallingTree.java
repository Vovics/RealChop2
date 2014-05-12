package ru.myhit.realchop2;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import ru.myhit.realchop2.RealchopListener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFallingTree extends Entity{
	public int blockID;
	public int metadata;
	/** stages: 0 - fall down up to fallDistance, 1 - rotate around base point, 2 - fall down until ground is reached**/
	public byte stage;
	/** how long first stage is **/
	public byte distanceToFall;
	/** height of the tree **/
	public int height;
	/** center of rotation coordinates. Rotate all tree parts around it**/
	public float baseX;
	public float baseY;
	public float baseZ;
	public double baseOffsetX;
	public double baseOffsetY;
	public double baseOffsetZ;
	/**direction of falling and rotating **/
	public byte direction;
	public float rotationSpeed;

    public EntityFallingTree(World par1World)
    {
    	 super(par1World);
         this.preventEntitySpawning = false;
    	 DataWatcher dw = this.getDataWatcher();
    	 dw.addObject(10, Integer.valueOf(0));
    	 dw.addObject(11, Integer.valueOf(0));
    	 dw.addObject(12, Byte.valueOf((byte)0));
    	 dw.addObject(13, Float.valueOf(0f));
    	 dw.addObject(14, Float.valueOf(0f));
    	 dw.addObject(15, Float.valueOf(0f));
    	 dw.addObject(16, Byte.valueOf((byte)0));
    	 dw.addObject(17, Integer.valueOf((byte)0));
         this.rotationSpeed=0;
    }
    
	public EntityFallingTree(World par1World, ChunkCoordinates parPosition, int parBlockId, int parMetadata, int parDistanceToFall, ChunkCoordinates parBasePoint, int parDirection, int parHeight) {
        super(par1World);
        this.blockID = parBlockId;
        this.metadata = parMetadata;
        this.preventEntitySpawning = false;
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = parPosition.posX + 0.5f;
        this.prevPosY = parPosition.posY + 0.5f;
        this.prevPosZ = parPosition.posZ + 0.5f;
        this.fallDistance=0;
        this.distanceToFall=(byte) parDistanceToFall;
        this.height=parHeight;
        this.rotationSpeed=0;
        this.direction=(byte) (parDirection%4);
        this.baseX=parBasePoint.posX + 0.5f + 0.5f*Direction.offsetX[this.direction];
        this.baseY=parBasePoint.posY;
        this.baseZ=parBasePoint.posZ + 0.5f + 0.5f*Direction.offsetZ[this.direction];
        this.setLocationAndAngles(parPosition.posX + 0.5f, parPosition.posY + 0.5f, parPosition.posZ + 0.5f,0,0);
        DataWatcher dw = this.getDataWatcher();
        /** additional data: 10 - blockID, 11 - block metadata, 12 - fallDistance, 13 - baseX, 14 - baseY, 15 - baseZ, 16 - direction, 17 - height**/
        dw.addObject(10, Integer.valueOf(this.blockID));
        dw.addObject(11, Integer.valueOf(this.metadata));
        dw.addObject(12, Byte.valueOf(this.distanceToFall));
        dw.addObject(13, Float.valueOf((float)this.baseX));
        dw.addObject(14, Float.valueOf((float)this.baseY));
        dw.addObject(15, Float.valueOf((float)this.baseZ));
        dw.addObject(16, Byte.valueOf(this.direction));
        dw.addObject(17, Integer.valueOf(this.height));
    }
	@Override
	public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.prevRotationPitch = this.rotationPitch;
        this.prevRotationYaw = this.rotationYaw;

		if (this.ticksExisted==1) {
			if (this.worldObj.isRemote) {
				DataWatcher dw = this.getDataWatcher();
				this.blockID=dw.getWatchableObjectInt(10);
				this.metadata=dw.getWatchableObjectInt(11);
				this.distanceToFall=dw.getWatchableObjectByte(12);
				this.baseX=dw.getWatchableObjectFloat(13);
				this.baseY=dw.getWatchableObjectFloat(14);
				this.baseZ=dw.getWatchableObjectFloat(15);
				this.direction=dw.getWatchableObjectByte(16);
				this.height=dw.getWatchableObjectInt(17);
			}
			baseOffsetX = this.posX-this.baseX;
			baseOffsetY = this.posY-this.baseY;
			baseOffsetZ = this.posZ-this.baseZ;
			if (this.height<3) {
				this.motionY=0;
				this.stage=2;
				return;
			}
			return;
		} 
        if (this.stage==0) {
        	if (this.fallDistance - this.motionY < this.distanceToFall) { 
        		if (!this.moveEntityWithCollisions(0, this.motionY, 0)) {
        			this.stage=2;
        			this.motionY=0;
        			return;
        		}
        		this.fallDistance -= this.motionY;
        		this.motionY -= 0.03999999910593033D;
        	} else {
        		if (!this.moveEntityWithCollisions(0, -(this.distanceToFall - this.fallDistance), 0)) {
        			this.stage=2;
        			this.motionY=0;
        			return;
        		}
        		this.motionY=0;
        		this.stage=1;
        		return;
        	}
        }
        if (this.stage==1) {
        	if (this.rotationYaw+this.rotationSpeed<90) {
            	this.setRotation(rotationYaw+this.rotationSpeed, 0);
            	this.rotationSpeed+=(1f/height);
        	} else {
        		this.setRotation(90, 0);
        		this.motionY = -0.03*this.height;
        		this.stage=2;
        	}
        	float fSinX=MathHelper.sin((float) Math.toRadians(angleFix(this.rotationYaw*Direction.offsetX[this.direction])));
        	float fSinZ=MathHelper.sin((float) Math.toRadians(angleFix(this.rotationYaw*Direction.offsetZ[this.direction])));
        	float fCosX=MathHelper.cos((float) Math.toRadians(angleFix(this.rotationYaw*Direction.offsetX[this.direction])));
        	float fCosZ=MathHelper.cos((float) Math.toRadians(angleFix(this.rotationYaw*Direction.offsetZ[this.direction])));
        	float fCos=MathHelper.cos((float) Math.toRadians(angleFix(this.rotationYaw)));
        	double dX = this.baseOffsetX * fCosX + this.baseOffsetY * fSinX;
            double dY = this.baseOffsetY * fCos - this.baseOffsetX * fSinX - this.baseOffsetZ * fSinZ;
            double dZ = this.baseOffsetZ * fCosZ + this.baseOffsetY * fSinZ;
            double offsetX=this.baseX+dX-this.posX;
            double offsetY=this.baseY-this.distanceToFall+dY-this.posY;
            double offsetZ=this.baseZ+dZ-this.posZ;
            if (!this.moveEntityWithCollisions(offsetX, offsetY, offsetZ)) {
        		this.stage=2;
        		return;
            }
        }
        if (this.stage==2) {
        		if (!this.moveEntityWithCollisions(0, this.motionY, 0)) {
        			this.dieWithPlace();
        			return;
        		}
        		this.motionY -= 0.03999999910593033D;
        }
		if (this.ticksExisted>200) {
			this.setDead();
		}
	}
	private float angleFix(double a) {
		a = a % 360d;
		if (a<0) a+=360;
		return (float) a;
	}
	@Override
    public void setLocationAndAngles(double parX, double parY, double parZ, float parYaw, float parPitch) {
		this.setPositionAndRotation(parX, parY, parZ, parYaw, parPitch);
	}
	@Override
	public void setPositionAndRotation2(double parX, double parY, double parZ, float parYaw, float parPitch, int par9) {
		this.setPositionAndRotation(parX, parY, parZ, parYaw, parPitch);
	}
	@Override
    public void setPositionAndRotation(double parX, double parY, double parZ, float par7, float par8)
    {
        this.setPosition(parX, parY, parZ);
        this.setRotation(par7, par8);
    }

	
	@Override
    public void setPosition(double parX, double parY, double parZ)
    {
        this.posX = parX;
        this.posY = parY;
        this.posZ = parZ;
        this.boundingBox.setBounds(parX - 0.5f, parY - 0.5f, parZ - 0.5f, parX + 0.5f, parY + 0.5f, parZ + 0.5f);
    }
    protected void setRotation(float par1, float par2)
    {
        this.rotationYaw = par1 % 360.0F;
    	if (this.rotationYaw<0) this.rotationYaw+=360f;
        this.rotationPitch = par2 % 360.0F;
    }
	public boolean moveEntityWithCollisions(double parX, double parY, double parZ) {
    	int testX =  MathHelper.floor_double(this.posX + parX - 0.5f - 0.05f * Math.signum(parX));
    	int testY =  MathHelper.floor_double(this.posY + parY - 0.48f);
    	int testZ =  MathHelper.floor_double(this.posZ + parZ - 0.5f - 0.05f * Math.signum(parZ));
    	Material mat=this.worldObj.getBlockMaterial(testX, testY , testZ);
    	this.moveEntity(parX, parY, parZ);
    	if (mat == mat.air) {
    		//this.moveEntity(parX, parY, parZ);
			return true;
    	}	
    	if (this.getMaterial() == Material.wood && RealchopListener.isLightBlock(mat)) {
    		this.DestroyBlock(testX, testY, testZ);
    		//this.moveEntity(parX, parY, parZ);
    		return true;
    	}
    	return false;
    }
    
    @Override
	public void moveEntity(double parX, double parY, double parZ) {
		this.boundingBox.offset(parX, parY, parZ);
 			this.posX += parX;
			this.posY += parY;
			this.posZ += parZ;
	}	
    public Material getMaterial() {
    	return this.blockID==0 ? Material.air : Block.blocksList[this.blockID].blockMaterial;
    }
    public void DestroyBlock(int x, int y, int z) {
    	int bid = this.worldObj.getBlockId(x, y, z);
    	if (bid==0) return;
    	int bmetadata = this.worldObj.getBlockMetadata(x,  y,  z);
    	this.worldObj.setBlockToAir(x, y, z);
    	Block.blocksList[bid].dropBlockAsItem(this.worldObj, x, y, z, bmetadata, 0);
    }
    public void dieWithPlace() {
    	int eX=MathHelper.floor_double(this.posX-0.5f);
    	int eY=MathHelper.floor_double(this.posY-0.5f);
    	int eZ=MathHelper.floor_double(this.posZ-0.5f);

    	Material myMat=this.getMaterial();
    	this.setDead();
		int ty=0;
		while(true) {
			Material mat = this.worldObj.getBlockMaterial(eX, eY + ty, eZ);
			if (mat == Material.air) break;
			if (myMat == Material.wood && mat == Material.leaves) {
				DestroyBlock(eX, eY + ty, eZ);
				break;
			}
			ty+=1;
		}
		if ((myMat == Material.leaves && ty>1) || eY + ty > 254) {
			Block.blocksList[this.blockID].dropBlockAsItem(this.worldObj, eX, eY, eZ, this.metadata, 0);
			return;
		}
    	if (myMat == Material.leaves) {
    		this.metadata = this.metadata | 8; //decay leaves
    	}
    	if (myMat==Material.wood) {
			if (this.rotationYaw>45) {
				if ((this.metadata & 12) > 0) {
					this.metadata = (this.metadata & 3);
				} else {
					this.metadata = ((this.metadata & 3) | ((this.direction==0 || this.direction==2)?8:4));
				}
			}
    	}
    	this.worldObj.setBlock(eX, eY + ty, eZ, this.blockID, this.metadata, 3);
    	
    }

    public World getWorld()
    {
        return this.worldObj;
    }

    @Override
	protected void entityInit() {}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
        this.blockID = nbtTagCompound.getInteger("blockID");
        this.metadata = nbtTagCompound.getInteger("metadata");
        this.stage= nbtTagCompound.getByte("stage");
        this.fallDistance= nbtTagCompound.getFloat("fallDistance");
        this.distanceToFall= nbtTagCompound.getByte("distanceToFall");
		this.baseOffsetX= nbtTagCompound.getDouble("baseOffsetX");
		this.baseOffsetY= nbtTagCompound.getDouble("baseOffsetY");
		this.baseOffsetZ= nbtTagCompound.getDouble("baseOffsetZ");		
		this.baseX= nbtTagCompound.getFloat("baseX");
		this.baseY= nbtTagCompound.getFloat("baseY");
		this.baseZ= nbtTagCompound.getFloat("baseZ");
		this.direction= nbtTagCompound.getByte("direction");
		this.height= nbtTagCompound.getInteger("height");
        
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
        nbtTagCompound.setInteger("blockID", this.blockID);
        nbtTagCompound.setInteger("metadata", this.metadata);
        nbtTagCompound.setByte("stage", this.stage);
        nbtTagCompound.setFloat("fallDistance", this.fallDistance);
		nbtTagCompound.setByte("distanceToFall", this.distanceToFall);
		nbtTagCompound.setDouble("baseOffsetX", this.baseOffsetX);
		nbtTagCompound.setDouble("baseOffsetY", this.baseOffsetY);
		nbtTagCompound.setDouble("baseOffsetZ", this.baseOffsetZ);		
		nbtTagCompound.setFloat("baseX", this.baseX);
		nbtTagCompound.setFloat("baseY", this.baseY);
		nbtTagCompound.setFloat("baseZ", this.baseZ);
		nbtTagCompound.setByte("direction", this.direction);
		nbtTagCompound.setInteger("height", this.height);
	}
    @SideOnly(Side.CLIENT)
    public float getShadowSize()
    {
        return 0.5F;
    }

}
