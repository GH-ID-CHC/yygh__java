import org.springframework.util.StringUtils;

/**
 * @program: yygh
 * @author:
 * @create: 2023-04-15 18:29
 **/
public class Test {
    public static void main(String[] args) {
        System.out.println(" 123".replace(" ", "+"));
        String a=new String();
        System.out.println(StringUtils.isEmpty(a));
        System.out.println(a==null);
    }
}
