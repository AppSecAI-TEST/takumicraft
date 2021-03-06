package com.tntmodders.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.*;

import static org.objectweb.asm.Opcodes.*;

public class TakumiASMTransformer implements IClassTransformer {

    //IClassTransformerにより呼ばれる書き換え用のメソッド。
    //EntityCreeper/explode(from EntityCreeper/onUpdate)→TakumiASMEvents/takumiExplode
    //(もしクラスがEntityCreeperならばリフレクションでexplodeを呼び出し、そうでないなら実装したtakumiExplodeを呼ぶ)
    //IClassTransformerにより呼ばれる書き換え用のメソッド。
    @Override
    public byte[] transform(final String name, final String transformedName, byte[] bytes) {
        //対象クラス以外を除外する。対象は呼び出し元があるクラスである。
        if ("net.minecraft.entity.monster.EntityCreeper".equals(transformedName)) {
            return this.transformEntityCreeper(name, transformedName, bytes);
        }
        return bytes;
    }

    private byte[] transformEntityCreeper(final String name, final String transformedName, byte[] bytes) {
        ClassReader cr = new ClassReader(bytes);
        ClassWriter cw = new ClassWriter(1);
        ClassVisitor cv = new ClassVisitor(ASM4, cw) {
            //クラス内のメソッドを訪れる。
            @Override
            public MethodVisitor visitMethod(int access, String methodName, String desc, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, methodName, desc, signature, exceptions);
                //呼び出し元のメソッドを参照していることを確認する。
                String s1 = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(name, methodName, desc);
                //C:\Users\<ユーザー名>\.gradle\caches\minecraft\net\minecraftforge\forge\1.7.10-10.13.4.1558-1.7.10\forge-1.7.10-10.13.4.1558-1.7.10-decomp.jar\より名称を検索、比較してメソッドの難読化名を探す。
                if (s1.equals("onUpdate")/* || s1.equals("func_145845_h")*/ || methodName.equals("onUpdate") /*|| methodName.equals("func_145845_h")*/) {
                    //もし対象だったらMethodVisitorを差し替える。
                    mv = new MethodVisitor(ASM4, mv) {
                        //呼び出す予定のメソッドを読み込む。
                        @Override
                        public void visitMethodInsn(int opcode, String owner, String methodName, String desc, boolean itf) {
                            //書き換え対象のメソッドであることを確認する。
                            String s2 = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(name, methodName, desc);
                            if (s2.equals("explode") /*|| s2.equals("func_145950_i")*/ || methodName.equals("explode") /*|| methodName.equals("func_145950_i")*/) {
                                //引数として次に渡す値にthisを指定する。
                                mv.visitVarInsn(ALOAD, 0);
                                //メソッドを読み込む。INVOKESTATICでstaticメソッドを呼び出す。
                                super.visitMethodInsn(INVOKESTATIC, "com/tntmodders/asm/TakumiASMHooks", "TakumiExplodeHook", Type.getMethodDescriptor(Type.VOID_TYPE, Type.getObjectType("net/minecraft/entity/monster/EntityCreeper")), false);
                                //今回はフックを差し込むだけだが、ここで書き換えも出来る。
                            }
                            //今回は最後に元のクラスを読み込んでreturnする。
                            super.visitMethodInsn(opcode, owner, methodName, desc, itf);
                        }
                    };
                }
                return mv;
            }
        };
        cr.accept(cv, ClassReader.EXPAND_FRAMES);
        return cw.toByteArray();
    }
}


