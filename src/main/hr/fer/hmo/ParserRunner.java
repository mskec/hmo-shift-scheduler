package hr.fer.hmo;

import hr.fer.hmo.data.Instance;

import java.io.FileNotFoundException;

public class ParserRunner {

  public static void main(String[] args) throws FileNotFoundException {
    System.out.println("Parser started...");

    Instance instance = InstanceParser.parse("instance.txt");

    System.out.println("Parsing done!");
  }

}
