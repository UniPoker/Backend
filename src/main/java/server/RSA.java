package server;
import org.json.JSONObject;

import java.math.*;
import java.util.Random;
public class RSA {

    private BigInteger prime_q, prime_p, prime_product, public_key, private_key, eule;
    private Random random;
    String decrypted;
    String[] string;
    BigInteger[] encrypted;

    protected JSONObject createKeys(){
        random = new Random();
        prime_p = new BigInteger(1024,100, random); //...
        prime_q = new BigInteger(1024,100, random); //...
        prime_product = prime_p.multiply(prime_q);	//produkt von prime_p und prime_q
        eule = (prime_p.subtract(BigInteger.ONE)).multiply((prime_q.subtract(BigInteger.ONE))); //phi-funktion
        public_key = new BigInteger(1024,100, random);  //teil des öffentlichen schlüssels
        while (eule.gcd(public_key).compareTo(BigInteger.ONE) > 0 && public_key.compareTo(eule) < 0 ) {  //gucken ob public_key teilerfremd ist
            public_key.add(BigInteger.ONE);
        }
        try{
            private_key = public_key.modInverse(eule); //versuch private_key herauszufinden
        }
        catch(ArithmeticException e){ //falls nicht möglich, erneuter aufruf
            createKeys();
        }
        //System.out.println("Öffentlicher Schlüssel: "+public_key+" /                     "+prime_product);
        //System.out.println("Privater Schlüssel: "+private_key+" /                     "+prime_product);
        JSONObject response = new JSONObject();
        response.put("public_key", public_key);
        response.put("private_key", private_key);
        response.put("prime_product", prime_product);
        return response;
    }

    //verschlüsseln
    protected JSONObject encrypt(String input, BigInteger public_key, BigInteger prime_product){
        char[] a = input.toCharArray();	//string in char umwandeln
        Integer[] input_numbers = new Integer[a.length];	//zwischenschritt zum biginteger
        string = new String[a.length];	//ist für das biginteger notwendig
        BigInteger [] big = new BigInteger[a.length];	//erklärt sich von selbst
        encrypted = new BigInteger[a.length];	//da wird der verschlüsselte text gespeichert
        for(int i=0;i<a.length;i++){
            input_numbers[i]=(int)a[i];	//hier werden die chars zum integer, ich benutze hier integer und nicht int, weil int nicht zu string umwandelbar ist
            string[i]=input_numbers[i].toString();	//die integer in Strings umwandeln
            big[i]=new BigInteger(string[i]);	//bigintegers erzeugen , damit ich damit rechnen kann
            //System.out.print(" "+big[i]); //zum testen obs klappt
            encrypted[i] = big[i].modPow(public_key, prime_product);
        }
        //System.out.println(" Dein Text wurde verschlüsselt");
        JSONObject response = new JSONObject();
        response.put("encrypted", encrypted);
        return response;
    }

    protected JSONObject entschlüsseln(BigInteger [] encrypted){
        decrypted = "";
        BigInteger[] bigKlarZahlen = new BigInteger[this.encrypted.length];	//hier werden die entschlüsselten zahlen gespeichert
        char[]a= new char[this.encrypted.length];	//wird zum ausgeben und umwandeln benötig
        for(int i=0; i< this.encrypted.length; i++){
            bigKlarZahlen[i] = this.encrypted[i].modPow(private_key, prime_product);	//zahlen werden entschlüsselt
            a[i]=(char)bigKlarZahlen[i].intValue();	//mit einem cast auf char werden die Zahlen wieder in buchstaben umgewandelt
            System.out.print(""+a[i]);	//wird jeder buchstabe einzelnd ausgegeben
            decrypted = decrypted +a[i];	//hier wird es zuerst in einem String gespeichert, damit dieser zum beispiel für ein TextFEld verwendet werden kann
        }
        //System.out.println(decrypted);
        JSONObject response = new JSONObject();
        response.put("decrypted", decrypted);
        return response;
    }
}
