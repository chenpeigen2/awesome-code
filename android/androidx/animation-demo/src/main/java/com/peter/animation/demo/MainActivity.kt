package com.peter.animation.demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.animation.demo.databinding.ActivityMainBinding
import com.peter.animation.demo.propertyanimation.AnimatorSetActivity
import com.peter.animation.demo.propertyanimation.ObjectAnimatorActivity
import com.peter.animation.demo.propertyanimation.ValueAnimatorActivity
import com.peter.animation.demo.physicsanimation.FlingAnimationActivity
import com.peter.animation.demo.physicsanimation.SpringAnimationActivity
import com.peter.animation.demo.materialshape.MaterialShapeDrawableActivity
import com.peter.animation.demo.transitionanimation.ActivityTransitionActivity
import com.peter.animation.demo.transitionanimation.FragmentTransitionActivity
import com.peter.animation.demo.transitionanimation.SharedElementActivity
import com.peter.animation.demo.viewanimation.FrameAnimationActivity
import com.peter.animation.demo.viewanimation.TweenAnimationActivity

/**
 * 动画Demo主页面
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: AnimationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadAnimationItems()
    }

    private fun setupRecyclerView() {
        adapter = AnimationAdapter { item ->
            val intent = Intent(this, item.targetClass)
            startActivity(intent)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun loadAnimationItems() {
        val items = listOf(
            // View Animation
            AnimationItem(
                title = getString(R.string.tween_animation),
                description = getString(R.string.tween_animation_desc),
                category = AnimationCategory.VIEW_ANIMATION,
                targetClass = TweenAnimationActivity::class.java
            ),
            AnimationItem(
                title = getString(R.string.frame_animation),
                description = getString(R.string.frame_animation_desc),
                category = AnimationCategory.VIEW_ANIMATION,
                targetClass = FrameAnimationActivity::class.java
            ),

            // Property Animation
            AnimationItem(
                title = getString(R.string.object_animator),
                description = getString(R.string.object_animator_desc),
                category = AnimationCategory.PROPERTY_ANIMATION,
                targetClass = ObjectAnimatorActivity::class.java
            ),
            AnimationItem(
                title = getString(R.string.value_animator),
                description = getString(R.string.value_animator_desc),
                category = AnimationCategory.PROPERTY_ANIMATION,
                targetClass = ValueAnimatorActivity::class.java
            ),
            AnimationItem(
                title = getString(R.string.animator_set),
                description = getString(R.string.animator_set_desc),
                category = AnimationCategory.PROPERTY_ANIMATION,
                targetClass = AnimatorSetActivity::class.java
            ),

            // Transition Animation
            AnimationItem(
                title = getString(R.string.activity_transition),
                description = getString(R.string.activity_transition_desc),
                category = AnimationCategory.TRANSITION_ANIMATION,
                targetClass = ActivityTransitionActivity::class.java
            ),
            AnimationItem(
                title = getString(R.string.fragment_transition),
                description = getString(R.string.fragment_transition_desc),
                category = AnimationCategory.TRANSITION_ANIMATION,
                targetClass = FragmentTransitionActivity::class.java
            ),
            AnimationItem(
                title = getString(R.string.shared_element),
                description = getString(R.string.shared_element_desc),
                category = AnimationCategory.TRANSITION_ANIMATION,
                targetClass = SharedElementActivity::class.java
            ),

            // Physics Animation
            AnimationItem(
                title = getString(R.string.spring_animation),
                description = getString(R.string.spring_animation_desc),
                category = AnimationCategory.PHYSICS_ANIMATION,
                targetClass = SpringAnimationActivity::class.java
            ),
            AnimationItem(
                title = getString(R.string.fling_animation),
                description = getString(R.string.fling_animation_desc),
                category = AnimationCategory.PHYSICS_ANIMATION,
                targetClass = FlingAnimationActivity::class.java
            ),

            // Material Shape
            AnimationItem(
                title = getString(R.string.material_shape_drawable),
                description = getString(R.string.material_shape_drawable_desc),
                category = AnimationCategory.MATERIAL_SHAPE,
                targetClass = MaterialShapeDrawableActivity::class.java
            )
        )

        adapter.submitList(items)
    }
}
