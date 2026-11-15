// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'matrix.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;
/// @nodoc
mixin _$MatrixOutcome {





@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is MatrixOutcome);
}


@override
int get hashCode => runtimeType.hashCode;

@override
String toString() {
  return 'MatrixOutcome()';
}


}

/// @nodoc
class $MatrixOutcomeCopyWith<$Res>  {
$MatrixOutcomeCopyWith(MatrixOutcome _, $Res Function(MatrixOutcome) __);
}


/// Adds pattern-matching-related methods to [MatrixOutcome].
extension MatrixOutcomePatterns on MatrixOutcome {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>({TResult Function( MatrixOutcome_Success value)?  success,TResult Function( MatrixOutcome_Failure value)?  failure,required TResult orElse(),}){
final _that = this;
switch (_that) {
case MatrixOutcome_Success() when success != null:
return success(_that);case MatrixOutcome_Failure() when failure != null:
return failure(_that);case _:
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

@optionalTypeArgs TResult map<TResult extends Object?>({required TResult Function( MatrixOutcome_Success value)  success,required TResult Function( MatrixOutcome_Failure value)  failure,}){
final _that = this;
switch (_that) {
case MatrixOutcome_Success():
return success(_that);case MatrixOutcome_Failure():
return failure(_that);}
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>({TResult? Function( MatrixOutcome_Success value)?  success,TResult? Function( MatrixOutcome_Failure value)?  failure,}){
final _that = this;
switch (_that) {
case MatrixOutcome_Success() when success != null:
return success(_that);case MatrixOutcome_Failure() when failure != null:
return failure(_that);case _:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>({TResult Function( Matrix matrix)?  success,TResult Function( MatrixErrorKind kind,  String message)?  failure,required TResult orElse(),}) {final _that = this;
switch (_that) {
case MatrixOutcome_Success() when success != null:
return success(_that.matrix);case MatrixOutcome_Failure() when failure != null:
return failure(_that.kind,_that.message);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>({required TResult Function( Matrix matrix)  success,required TResult Function( MatrixErrorKind kind,  String message)  failure,}) {final _that = this;
switch (_that) {
case MatrixOutcome_Success():
return success(_that.matrix);case MatrixOutcome_Failure():
return failure(_that.kind,_that.message);}
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>({TResult? Function( Matrix matrix)?  success,TResult? Function( MatrixErrorKind kind,  String message)?  failure,}) {final _that = this;
switch (_that) {
case MatrixOutcome_Success() when success != null:
return success(_that.matrix);case MatrixOutcome_Failure() when failure != null:
return failure(_that.kind,_that.message);case _:
  return null;

}
}

}

/// @nodoc


class MatrixOutcome_Success extends MatrixOutcome {
  const MatrixOutcome_Success({required this.matrix}): super._();
  

 final  Matrix matrix;

/// Create a copy of MatrixOutcome
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$MatrixOutcome_SuccessCopyWith<MatrixOutcome_Success> get copyWith => _$MatrixOutcome_SuccessCopyWithImpl<MatrixOutcome_Success>(this, _$identity);



@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is MatrixOutcome_Success&&(identical(other.matrix, matrix) || other.matrix == matrix));
}


@override
int get hashCode => Object.hash(runtimeType,matrix);

@override
String toString() {
  return 'MatrixOutcome.success(matrix: $matrix)';
}


}

/// @nodoc
abstract mixin class $MatrixOutcome_SuccessCopyWith<$Res> implements $MatrixOutcomeCopyWith<$Res> {
  factory $MatrixOutcome_SuccessCopyWith(MatrixOutcome_Success value, $Res Function(MatrixOutcome_Success) _then) = _$MatrixOutcome_SuccessCopyWithImpl;
@useResult
$Res call({
 Matrix matrix
});




}
/// @nodoc
class _$MatrixOutcome_SuccessCopyWithImpl<$Res>
    implements $MatrixOutcome_SuccessCopyWith<$Res> {
  _$MatrixOutcome_SuccessCopyWithImpl(this._self, this._then);

  final MatrixOutcome_Success _self;
  final $Res Function(MatrixOutcome_Success) _then;

/// Create a copy of MatrixOutcome
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') $Res call({Object? matrix = null,}) {
  return _then(MatrixOutcome_Success(
matrix: null == matrix ? _self.matrix : matrix // ignore: cast_nullable_to_non_nullable
as Matrix,
  ));
}


}

/// @nodoc


class MatrixOutcome_Failure extends MatrixOutcome {
  const MatrixOutcome_Failure({required this.kind, required this.message}): super._();
  

 final  MatrixErrorKind kind;
 final  String message;

/// Create a copy of MatrixOutcome
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$MatrixOutcome_FailureCopyWith<MatrixOutcome_Failure> get copyWith => _$MatrixOutcome_FailureCopyWithImpl<MatrixOutcome_Failure>(this, _$identity);



@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is MatrixOutcome_Failure&&(identical(other.kind, kind) || other.kind == kind)&&(identical(other.message, message) || other.message == message));
}


@override
int get hashCode => Object.hash(runtimeType,kind,message);

@override
String toString() {
  return 'MatrixOutcome.failure(kind: $kind, message: $message)';
}


}

/// @nodoc
abstract mixin class $MatrixOutcome_FailureCopyWith<$Res> implements $MatrixOutcomeCopyWith<$Res> {
  factory $MatrixOutcome_FailureCopyWith(MatrixOutcome_Failure value, $Res Function(MatrixOutcome_Failure) _then) = _$MatrixOutcome_FailureCopyWithImpl;
@useResult
$Res call({
 MatrixErrorKind kind, String message
});




}
/// @nodoc
class _$MatrixOutcome_FailureCopyWithImpl<$Res>
    implements $MatrixOutcome_FailureCopyWith<$Res> {
  _$MatrixOutcome_FailureCopyWithImpl(this._self, this._then);

  final MatrixOutcome_Failure _self;
  final $Res Function(MatrixOutcome_Failure) _then;

/// Create a copy of MatrixOutcome
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') $Res call({Object? kind = null,Object? message = null,}) {
  return _then(MatrixOutcome_Failure(
kind: null == kind ? _self.kind : kind // ignore: cast_nullable_to_non_nullable
as MatrixErrorKind,message: null == message ? _self.message : message // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
