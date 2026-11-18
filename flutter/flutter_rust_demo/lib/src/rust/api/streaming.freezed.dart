// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'streaming.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;
/// @nodoc
mixin _$ProgressEvent {





@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is ProgressEvent);
}


@override
int get hashCode => runtimeType.hashCode;

@override
String toString() {
    return 'ProgressEvent()';
}


}

/// @nodoc
class $ProgressEventCopyWith<$Res>  {
$ProgressEventCopyWith(ProgressEvent _, $Res Function(ProgressEvent) __);
}


/// Adds pattern-matching-related methods to [ProgressEvent].
extension ProgressEventPatterns on ProgressEvent {
/// A variant of `map` that fallback to returning `orElse`.
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case final Subclass value:
///     return ...;
///   case _:
///     return orElse();
/// }
/// ```

@optionalTypeArgs TResult maybeMap<TResult extends Object?>({TResult Function( ProgressEvent_Progress value)?  progress,TResult Function( ProgressEvent_Done value)?  done,required TResult orElse(),}){
final _that = this;
switch (_that) {
case ProgressEvent_Progress() when progress != null:
return progress(_that);case ProgressEvent_Done() when done != null:
return done(_that);case _:
  return orElse();

}
}
/// A `switch`-like method, using callbacks.
///
/// Callbacks receives the raw object, upcasted.
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case final Subclass value:
///     return ...;
///   case final Subclass2 value:
///     return ...;
/// }
/// ```

@optionalTypeArgs TResult map<TResult extends Object?>({required TResult Function( ProgressEvent_Progress value)  progress,required TResult Function( ProgressEvent_Done value)  done,}){
final _that = this;
switch (_that) {
case ProgressEvent_Progress():
return progress(_that);case ProgressEvent_Done():
return done(_that);}
}
/// A variant of `map` that fallback to returning `null`.
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case final Subclass value:
///     return ...;
///   case _:
///     return null;
/// }
/// ```

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>({TResult? Function( ProgressEvent_Progress value)?  progress,TResult? Function( ProgressEvent_Done value)?  done,}){
final _that = this;
switch (_that) {
case ProgressEvent_Progress() when progress != null:
return progress(_that);case ProgressEvent_Done() when done != null:
return done(_that);case _:
  return null;

}
}
/// A variant of `when` that fallback to an `orElse` callback.
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case Subclass(:final field):
///     return ...;
///   case _:
///     return orElse();
/// }
/// ```

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>({TResult Function( PlatformInt64 done,  PlatformInt64 total,  double partialEstimate)?  progress,TResult Function( double estimate,  double absError,  PlatformInt64 elapsedMs)?  done,required TResult orElse(),}) {final _that = this;
switch (_that) {
case ProgressEvent_Progress() when progress != null:
return progress(_that.done,_that.total,_that.partialEstimate);case ProgressEvent_Done() when done != null:
return done(_that.estimate,_that.absError,_that.elapsedMs);case _:
  return orElse();

}
}
/// A `switch`-like method, using callbacks.
///
/// As opposed to `map`, this offers destructuring.
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case Subclass(:final field):
///     return ...;
///   case Subclass2(:final field2):
///     return ...;
/// }
/// ```

@optionalTypeArgs TResult when<TResult extends Object?>({required TResult Function( PlatformInt64 done,  PlatformInt64 total,  double partialEstimate)  progress,required TResult Function( double estimate,  double absError,  PlatformInt64 elapsedMs)  done,}) {final _that = this;
switch (_that) {
case ProgressEvent_Progress():
return progress(_that.done,_that.total,_that.partialEstimate);case ProgressEvent_Done():
return done(_that.estimate,_that.absError,_that.elapsedMs);}
}
/// A variant of `when` that fallback to returning `null`
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case Subclass(:final field):
///     return ...;
///   case _:
///     return null;
/// }
/// ```

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>({TResult? Function( PlatformInt64 done,  PlatformInt64 total,  double partialEstimate)?  progress,TResult? Function( double estimate,  double absError,  PlatformInt64 elapsedMs)?  done,}) {final _that = this;
switch (_that) {
case ProgressEvent_Progress() when progress != null:
return progress(_that.done,_that.total,_that.partialEstimate);case ProgressEvent_Done() when done != null:
return done(_that.estimate,_that.absError,_that.elapsedMs);case _:
  return null;

}
}

}

/// @nodoc


class ProgressEvent_Progress extends ProgressEvent {
  const ProgressEvent_Progress({required this.done, required this.total, required this.partialEstimate}): super._();
  

/// 已完成采样数
 final  PlatformInt64 done;
/// 总采样数
 final  PlatformInt64 total;
/// 当前部分估算值
 final  double partialEstimate;

/// Create a copy of ProgressEvent
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$ProgressEvent_ProgressCopyWith<ProgressEvent_Progress> get copyWith => _$ProgressEvent_ProgressCopyWithImpl<ProgressEvent_Progress>(this, _$identity);



@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is ProgressEvent_Progress&&(identical(other.done, done) || other.done == done)&&(identical(other.total, total) || other.total == total)&&(identical(other.partialEstimate, partialEstimate) || other.partialEstimate == partialEstimate));
}


@override
int get hashCode {
    return Object.hash(runtimeType,done,total,partialEstimate);
}

@override
String toString() {
    return 'ProgressEvent.progress(done: $done, total: $total, partialEstimate: $partialEstimate)';
}


}

/// @nodoc
abstract mixin class $ProgressEvent_ProgressCopyWith<$Res> implements $ProgressEventCopyWith<$Res> {
  factory $ProgressEvent_ProgressCopyWith(ProgressEvent_Progress value, $Res Function(ProgressEvent_Progress) _then) = _$ProgressEvent_ProgressCopyWithImpl;
@useResult
$Res call({
 PlatformInt64 done, PlatformInt64 total, double partialEstimate
});




}
/// @nodoc
class _$ProgressEvent_ProgressCopyWithImpl<$Res>
    implements $ProgressEvent_ProgressCopyWith<$Res> {
  _$ProgressEvent_ProgressCopyWithImpl(this._self, this._then);

  final ProgressEvent_Progress _self;
  final $Res Function(ProgressEvent_Progress) _then;

/// Create a copy of ProgressEvent
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') $Res call({Object? done = null,Object? total = null,Object? partialEstimate = null,}) {
  return _then(ProgressEvent_Progress(
done: null == done ? _self.done : done // ignore: cast_nullable_to_non_nullable
as PlatformInt64,total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as PlatformInt64,partialEstimate: null == partialEstimate ? _self.partialEstimate : partialEstimate // ignore: cast_nullable_to_non_nullable
as double,
  ));
}


}

/// @nodoc


class ProgressEvent_Done extends ProgressEvent {
  const ProgressEvent_Done({required this.estimate, required this.absError, required this.elapsedMs}): super._();
  

 final  double estimate;
/// 与真值 π 的绝对误差
 final  double absError;
 final  PlatformInt64 elapsedMs;

/// Create a copy of ProgressEvent
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$ProgressEvent_DoneCopyWith<ProgressEvent_Done> get copyWith => _$ProgressEvent_DoneCopyWithImpl<ProgressEvent_Done>(this, _$identity);



@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is ProgressEvent_Done&&(identical(other.estimate, estimate) || other.estimate == estimate)&&(identical(other.absError, absError) || other.absError == absError)&&(identical(other.elapsedMs, elapsedMs) || other.elapsedMs == elapsedMs));
}


@override
int get hashCode {
    return Object.hash(runtimeType,estimate,absError,elapsedMs);
}

@override
String toString() {
    return 'ProgressEvent.done(estimate: $estimate, absError: $absError, elapsedMs: $elapsedMs)';
}


}

/// @nodoc
abstract mixin class $ProgressEvent_DoneCopyWith<$Res> implements $ProgressEventCopyWith<$Res> {
  factory $ProgressEvent_DoneCopyWith(ProgressEvent_Done value, $Res Function(ProgressEvent_Done) _then) = _$ProgressEvent_DoneCopyWithImpl;
@useResult
$Res call({
 double estimate, double absError, PlatformInt64 elapsedMs
});




}
/// @nodoc
class _$ProgressEvent_DoneCopyWithImpl<$Res>
    implements $ProgressEvent_DoneCopyWith<$Res> {
  _$ProgressEvent_DoneCopyWithImpl(this._self, this._then);

  final ProgressEvent_Done _self;
  final $Res Function(ProgressEvent_Done) _then;

/// Create a copy of ProgressEvent
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') $Res call({Object? estimate = null,Object? absError = null,Object? elapsedMs = null,}) {
  return _then(ProgressEvent_Done(
estimate: null == estimate ? _self.estimate : estimate // ignore: cast_nullable_to_non_nullable
as double,absError: null == absError ? _self.absError : absError // ignore: cast_nullable_to_non_nullable
as double,elapsedMs: null == elapsedMs ? _self.elapsedMs : elapsedMs // ignore: cast_nullable_to_non_nullable
as PlatformInt64,
  ));
}


}

// dart format on
