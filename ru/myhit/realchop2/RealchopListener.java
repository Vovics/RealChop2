package ru.myhit.realchop2;

import java.util.HashMap;
import java.util.HashSet;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

public class RealchopListener {
	@ForgeSubscribe
	public void onBlockBreak(BreakEvent ev) {
		EntityPlayer p = ev.getPlayer();
		if (p == null ) return;
		if (!(p instanceof EntityPlayer)) return;
		Block breakBlock= ev.block;
		if (ev.block==null) return;
		int direction = Direction.rotateLeft[Direction.getMovementDirection(ev.x - p.posX, ev.z - p.posZ)];

		Material breakBlockType = breakBlock.blockMaterial;
		if (breakBlockType == Material.wood) {
			this.blockBreak(ev.world, new ChunkCoordinates(ev.x, ev.y, ev.z), direction);
		}
		//ev.setCanceled(true);
	}

	private void blockBreak(World w, ChunkCoordinates breakBlockCoordinates, int direction) {
	       int blockProcessingLimit=1000;
	        HashSet<ChunkCoordinates> tree = new HashSet<ChunkCoordinates>();
	        HashSet<ChunkCoordinates> solid = new HashSet<ChunkCoordinates>();
	        HashSet<ChunkCoordinates> search = new HashSet<ChunkCoordinates>();
	        HashSet<ChunkCoordinates> leaves = new HashSet<ChunkCoordinates>();
	        search.add(breakBlockCoordinates);
	        int breakBlockMetadata=getBlockMetadata(w, breakBlockCoordinates);

	        // filling tree
	        boolean findNext = true;
	        int limit = 0;
	        while (findNext) {
	            findNext = false;
	            HashSet<ChunkCoordinates> newSearch = new HashSet<ChunkCoordinates>();
	            for (ChunkCoordinates l : search) {
	                HashSet<ChunkCoordinates> near = getNearBlocks(w, l, 1);
	                for (ChunkCoordinates nearLocation : near) {
	                	if (nearLocation.equals(breakBlockCoordinates)) continue;
	                    Material nearBlockMaterial = getBlockMaterial(w, nearLocation);
	                    if (nearBlockMaterial == Material.wood ) {
	                    	if ((breakBlockMetadata & 3) != (getBlockMetadata(w, nearLocation) & 3)) continue;
	                        if (!tree.contains(nearLocation)) {
                                tree.add(nearLocation);
                                newSearch.add(nearLocation);
                                findNext = true;
	                        }
	                    }
	                    if (nearBlockMaterial != Material.wood && !isLightBlock(nearBlockMaterial)) {
	                        solid.add(nearLocation);
	                    }

	                }
	            }
	            limit++;
	            if (limit > blockProcessingLimit) {
	                break;
	            }
	            if (findNext) {
	                search.clear();
	                search.addAll(newSearch);
	            }

	        }
	        
	        //leaves
	        if (tree.size()==0 && getBlockMaterial(w, breakBlockCoordinates) == Material.wood) {
                HashSet<ChunkCoordinates> near = getNearBlocks(w, breakBlockCoordinates, 3);
                for (ChunkCoordinates nearLocation : near) {
                    Material nearBlockMaterial = getBlockMaterial(w, nearLocation);
                    if (nearBlockMaterial == Material.leaves) {
                    	if ((breakBlockMetadata & 3) != (getBlockMetadata(w, nearLocation) & 3)) continue;
                        if (!leaves.contains(nearLocation)) {
                            leaves.add(nearLocation);
                        }
                    }
                }
	        }

	        if (tree.size()>0) {
		        // Defiling tree depends on solid blocks
		        search.clear();
		        search.addAll(solid);
		        findNext = true;
		        limit = 0;
		        while (findNext) {
		            findNext = false;
		            HashSet<ChunkCoordinates> newSearch = new HashSet<ChunkCoordinates>();
		            for (ChunkCoordinates l : search) {
		                HashSet<ChunkCoordinates> near = getNearBlocks(w, l, 1);
		                for (ChunkCoordinates nearLocation : near) {
		                    if (getBlockMaterial(w, nearLocation) == Material.wood) {
		                        if (tree.contains(nearLocation)) {
		                            tree.remove(nearLocation);
		                            newSearch.add(nearLocation);
		                            findNext = true;
		                        }
		                    }
		                }
		            }
		            limit++;
		            if (limit > blockProcessingLimit) {
		                break;
		            }
		            if (findNext) {
		                search.clear();
		                search.addAll(newSearch);
		                newSearch.clear();
		            }
		        }
	        }
	        
            //leaves
	        for (ChunkCoordinates l: tree) {
	        	HashSet<ChunkCoordinates> near;
	        	switch (breakBlockMetadata & 3) {
	        		case 0: // oak
	        			near = getNearBlocks(w, l, 3);
	        			break;
	        		case 1: // Spruce
	        			near = getNearBlocks(w, l, 3);
	        			break;
	        		case 3: // jungle
	        			near = getNearBlocks(w, l, 5);
	        			break;
	        		default:
	        			near = getNearBlocks(w, l, 2);
	        			break;
	        	}
                for (ChunkCoordinates nearLocation : near) {
                    Material nearBlockMaterial = getBlockMaterial(w, nearLocation);
                    if (nearBlockMaterial == Material.leaves) {
                    	if ((breakBlockMetadata & 3) != (getBlockMetadata(w, nearLocation) & 3)) continue;
                        if (!leaves.contains(nearLocation)) {
                            leaves.add(nearLocation);
                        }
                    }
                }
	        }
	        
	        int height=0;
	        int width=0;
	        int fallDistance=0;
	        int minY=256;
	        int maxY=0;
	        int minZ=breakBlockCoordinates.posZ;
	        int maxZ=breakBlockCoordinates.posZ;
	        int minX=breakBlockCoordinates.posX;
	        int maxX=breakBlockCoordinates.posX;
	        HashMap<ChunkCoordinates, Integer> heightMap = new HashMap<ChunkCoordinates, Integer>();
	        ChunkCoordinates basepoint=new ChunkCoordinates(breakBlockCoordinates);
	        for (ChunkCoordinates l: tree) {
	        	if (l.posX < minX) minX=l.posX;
	        	if (l.posX > maxX) maxX=l.posX;
	        	if (l.posY < minY) minY=l.posY;
	        	if (l.posY > maxY) maxY=l.posY;
	        	if (l.posZ < minZ) minZ=l.posZ;
	        	if (l.posZ > maxZ) maxZ=l.posZ;

	        	ChunkCoordinates horL = new ChunkCoordinates (l.posX, 0, l.posZ);
	        	if (!heightMap.containsKey(horL) || heightMap.get(horL)>l.posY) {
	        		heightMap.put(horL, l.posY);
	        		int d=1;
	        		for (; l.posY-d>0 && d<64; d++) {
	        			ChunkCoordinates testLocation = new ChunkCoordinates(l.posX, l.posY-d, l.posZ);
	        			if (testLocation.equals(breakBlockCoordinates)) continue;
	        			if (!isLightBlock(getBlockMaterial(w, testLocation))) {
	        				break;
	        			}
	        		}
	        		if (fallDistance!=0 && d - 1 == fallDistance) {
	        			int oX=l.posX - basepoint.posX;
	        			int oZ=l.posZ - basepoint.posZ;
	        			oX=(oX==0)?0:(oX>0?1:-1);
	        			oZ=(oZ==0)?0:(oZ>0?1:-1);
	        			if ((oX!=0 && oX==Direction.offsetX[direction]) || (oZ!=0 && oZ==Direction.offsetZ[direction])) {
	        				basepoint=l;
	        			}
	        		}
	        		if (fallDistance == 0 || d - 1 < fallDistance) {
	        			fallDistance=d-1;
	        			basepoint=l;
	        		}
	        	}
            }
	        height=maxY-minY;
	        width=Math.max(Math.abs((maxX-minX)*Direction.offsetX[direction]),Math.abs((maxZ-minZ)*Direction.offsetZ[direction]));
	        if (height<0) height=0;
	        if (width>height) height=1;
	        for (ChunkCoordinates l : tree) {
            	int blockId=getBlockId(w, l);
            	int blockMetadata=getBlockMetadata(w, l);
	        	w.setBlockToAir(l.posX, l.posY, l.posZ);
            	w.spawnEntityInWorld(new EntityFallingTree(w, l, blockId, blockMetadata, fallDistance, basepoint, direction, height));
            }
	        for (ChunkCoordinates l : leaves) {
            	int blockId=getBlockId(w, l);
            	int blockMetadata=getBlockMetadata(w, l);
	        	w.setBlockToAir(l.posX, l.posY, l.posZ);
            	w.spawnEntityInWorld(new EntityFallingTree(w, l, blockId, blockMetadata, fallDistance, basepoint, direction, height));
            }
	        
	}
	private HashSet<ChunkCoordinates> getNearBlocks(World w, ChunkCoordinates l, int radius) {
	    HashSet<ChunkCoordinates> m = new HashSet<ChunkCoordinates>();
	    for (int z = l.posZ - radius; z <= l.posZ + radius; z++) {
	        for (int x = l.posX - radius; x <= l.posX + radius; x++) {
	            for (int y = l.posY - radius; y <= l.posY + radius; y++) {
	            	ChunkCoordinates tl = new ChunkCoordinates(x, y, z);
	                if (tl != l) {
	                    m.add(tl);
	                }
	            }
	        }
	    }
	    return m;
	}
	int getBlockId(World w, ChunkCoordinates l) {
		return w.getBlockId(l.posX, l.posY, l.posZ);
	}
	int getBlockMetadata(World w, ChunkCoordinates l) {
		return w.getBlockMetadata(l.posX, l.posY, l.posZ);
	}
	Material getBlockMaterial(World w, ChunkCoordinates l) {
		return w.getBlockMaterial(l.posX, l.posY, l.posZ);
	}
	public static boolean isLightBlock(Material m) {
        return m == Material.leaves || m == Material.air || m== Material.vine || m == Material.plants || m == Material.snow || m == Material.web;
    }	
}