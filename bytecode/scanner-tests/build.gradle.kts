kotlin {
  jvmToolchain(16)
}

dependencies {
  testImplementation(projects.bytecodeScanner)
  testImplementation(projects.bytecodeSamplesJava)
  testImplementation(projects.bytecodeSamplesKotlin)
  testImplementation(projects.bytecodeTesting)
}
