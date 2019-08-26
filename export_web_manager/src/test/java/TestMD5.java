import org.apache.shiro.crypto.hash.Md5Hash;
import org.junit.Test;
import sun.security.provider.MD5;

public class TestMD5 {
    @Test
    public void test() {
        System.out.println(new Md5Hash("1").toString());
    }

    // 加密加盐
    @Test
    public void md5Salt(){
        // 用户名
        String username = "mz@export.com";
        // 密码
        String password = "1";
        // 参数1：密码, 参数2：盐；把用户名作为盐
        Md5Hash encodePassword = new Md5Hash(password, username);
        // 老王的e1087d424b213621545713b872420c7b
        //老大的9e0a9ec1aaa9e418a417755e2140be60
        //张三ac0ebad6ca68c5c5340495caf6737582
        System.out.println("根据用户名作为盐，加密加盐后的结果：" + encodePassword);
    }
}
