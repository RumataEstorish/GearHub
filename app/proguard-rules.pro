-keep class gearsoftware.gearhub.di.providers.InternalServicesProvider

-keep @gearsoftware.gearhub.services.SapService public class *
-keep @gearsoftware.gearhub.services.SapPermissionService public class *
-keep class gearsoftware.gearhub.services.SapService.** { *; }
-keep class gearsoftware.gearhub.services.SapServicePermissions.** { *; }
-keepclassmembers class gearsoftware.gearhub.services.SapService.** { *; }
-keepattributes *Annotation*

-keepclassmembers class com.samsung.** { *;}
-keep class com.samsung.** { *; }
-dontwarn com.samsung.**
-keepattributes InnerClasses

-keepclassmembers class gearsoftware.gearhub.socialgear.SocialGearProviderService.** { *; }
-keep class gearsoftware.gearhub.socialgear.SocialGearProviderService { *; }

-keepclassmembers class gearsoftware.gearhub.socialgear.SocialGearServiceDescription.** { *; }
-keep class gearsoftware.gearhub.socialgear.SocialGearServiceDescription { *; }

-keepclassmembers class gearsoftware.gearhub.servicesimpl.enotegear.ENoteGearServiceProvider.** { *; }
-keep class gearsoftware.gearhub.servicesimpl.enotegear.ENoteGearServiceProvider { *; }
-keepclasseswithmembernames class gearsoftware.gearhub.servicesimpl.enotegear.ENoteGearServiceDescription.** { *; }
-keep class gearsoftware.gearhub.servicesimpl.enotegear.ENoteGearServiceDescription { *; }

-keepclassmembers class gearsoftware.gearhub.servicesimpl.squaregear.FourSquareAccessoryProvider.** { *; }
-keep class gearsoftware.gearhub.servicesimpl.squaregear.FourSquareAccessoryProvider { *; }
-keepclassmembers class gearsoftware.gearhub.servicesimpl.squaregear.SquareGearServiceDescription.** { *; }
-keep class gearsoftware.gearhub.servicesimpl.squaregear.SquareGearServiceDescription { *; }

-keepclassmembers class gearsoftware.gearhub.servicesimpl.taskgear.TaskGearServiceProvider.** { *; }
-keep class gearsoftware.gearhub.servicesimpl.taskgear.TaskGearServiceProvider { *; }
-keepclassmembers class gearsoftware.gearhub.servicesimpl.taskgear.TaskGearServiceDescription.** { *; }
-keep class gearsoftware.gearhub.servicesimpl.taskgear.TaskGearServiceDescription { *; }

-keepclassmembers class gearsoftware.gearhub.servicesimpl.todogear.TodoGearServiceProvider.** { *; }
-keep class gearsoftware.gearhub.servicesimpl.todogear.TodoGearServiceProvider { *; }
-keepclassmembers class gearsoftware.gearhub.servicesimpl.todogear.TodoGearServiceDescription.** { *; }
-keep class gearsoftware.gearhub.servicesimpl.todogear.TodoGearServiceDescription { *; }

-keepclassmembers class gearsoftware.gearhub.servicesimpl.transgear.TransGearProviderService.** { *; }
-keep class gearsoftware.gearhub.servicesimpl.transgear.TransGearProviderService { *; }
-keepclassmembers class gearsoftware.gearhub.servicesimpl.transgear.TransGearServiceDescription.** { *; }
-keep class gearsoftware.gearhub.servicesimpl.transgear.TransGearServiceDescription { *; }

#OKhttp
#noinspection ShrinkerUnresolvedReference
-keep class okhttp3.**
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-keep class kotlin.Metadata { *; }
# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

#Toothpick 3
-dontwarn javax.inject.**
-dontwarn javax.annotation.**
-keep class javax.inject.**
-keep class javax.annotation.**
-keepclassmembers class * {
	@javax.inject.Inject <init>(...);
	@javax.inject.Inject <init>();
	@javax.inject.Inject <fields>;
	public <init>(...);
}
-keepnames @toothpick.InjectConstructor class *
-keepclasseswithmembernames class * { toothpick.ktp.delegate.* *; }
-keepclassmembers class * {
    toothpick.ktp.delegate.* *;
}
-keep @javax.inject.Qualifier public class *
-dontwarn javax.xml.bind.DatatypeConverter