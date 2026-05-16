# Keep Gson data classes
-keepclassmembers class com.electricitybill.calculator.BillRecord { *; }
-keep class com.google.gson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
