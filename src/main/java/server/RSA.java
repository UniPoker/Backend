package server;
import java.math.*;
import java.util.Random;
public class RSA {

    private BigInteger q, p, N, e, d, eule;
    private Random rand;
    String test;
    String[] string;
    BigInteger[]geheimtext;

    public RSA(){
    }
    protected void SchlüsselErzeugen(){
        rand = new Random();
        p = new BigInteger(1024,100,rand); //...
        q = new BigInteger(1024,100,rand); //...
        N = p.multiply(q);	//produkt von p und q
        eule = (p.subtract(BigInteger.ONE)).multiply((q.subtract(BigInteger.ONE))); //phi-funktion
        e = new BigInteger(1024,100,rand);  //teil des öffentlichen schlüssels
        while (eule.gcd(e).compareTo(BigInteger.ONE) > 0 && e.compareTo(eule) < 0 ) {  //gucken ob e teilerfremd ist
            e.add(BigInteger.ONE);
        }
        try{
            d = e.modInverse(eule); //versuch d herauszufinden
        }
        catch(ArithmeticException e){ //falls nicht möglich, erneuter aufruf
            SchlüsselErzeugen();
        }
        //System.out.println("Öffentlicher Schlüssel: "+e+" /                     "+N);
        //System.out.println("Privater Schlüssel: "+d+" /                     "+N);
    }

    protected void verschlüsseln(String eingabe){
        char[] a = eingabe.toCharArray();	//string in char umwandeln
        Integer[] zahlen = new Integer[a.length];	//zwischenschritt zum biginteger
        string = new String[a.length];	//ist für das biginteger notwendig
        BigInteger [] big = new BigInteger[a.length];	//erklärt sich von selbst
        geheimtext = new BigInteger[a.length];	//da wird der verschlüsselte text gespeichert
        for(int i=0;i<a.length;i++){
            zahlen[i]=(int)a[i];	//hier werden die chars zum integer, ich benutze hier integer und nicht int, weil int nicht zu string umwandelbar ist
            string[i]=zahlen[i].toString();	//die integer in Strings umwandeln
            big[i]=new BigInteger(string[i]);	//bigintegers erzeugen , damit ich damit rechnen kann
            //System.out.print(" "+big[i]); //zum testen obs klappt
            geheimtext[i] = big[i].modPow(e, N);
        }
        //System.out.println(" Dein Text wurde verschlüsselt");
    }

    protected void entschlüsseln(){
        test = new String("");
        BigInteger[] bigKlarZahlen = new BigInteger[geheimtext.length];	//hier werden die entschlüsselten zahlen gespeichert
        char[]a= new char[geheimtext.length];	//wird zum ausgeben und umwandeln benötig
        for(int i=0; i<geheimtext.length; i++){
            bigKlarZahlen[i] = geheimtext[i].modPow(d, N);	//zahlen werden entschlüsselt
            a[i]=(char)bigKlarZahlen[i].intValue();	//mit einem cast auf char werden die Zahlen wieder in buchstaben umgewandelt
            System.out.print(""+a[i]);	//wird jeder buchstabe einzelnd ausgegeben
            test=test+a[i];	//hier wird es zuerst in einem String gespeichert, damit dieser zum beispiel für ein TextFEld verwendet werden kann
        }
        //System.out.println(test);
    }
}
