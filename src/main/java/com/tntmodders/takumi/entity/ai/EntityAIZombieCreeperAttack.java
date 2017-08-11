package com.tntmodders.takumi.entity.ai;

import com.tntmodders.takumi.entity.mobs.EntityZombieCreeper;
import net.minecraft.entity.ai.EntityAIAttackMelee;

public class EntityAIZombieCreeperAttack extends EntityAIAttackMelee {
    private final EntityZombieCreeper zombie;
    private int raiseArmTicks;

    public EntityAIZombieCreeperAttack(EntityZombieCreeper zombieIn, double speedIn, boolean longMemoryIn) {
        super(zombieIn, speedIn, longMemoryIn);
        this.zombie = zombieIn;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
        super.startExecuting();
        this.raiseArmTicks = 0;
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask() {
        super.resetTask();
        this.zombie.setArmsRaised(false);
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void updateTask() {
        super.updateTask();
        ++this.raiseArmTicks;

        if (this.raiseArmTicks >= 5 && this.attackTick < 10) {
            this.zombie.setArmsRaised(true);
        } else {
            this.zombie.setArmsRaised(false);
        }
    }
}