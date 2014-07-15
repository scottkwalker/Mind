Scott Walker
============
Hello! I'm an developer based in London. I have a lot of experience working in Agile, TDD environments. I really enjoy functional programming and have put together this Scala application as a way to practice techniques.

You can find out more about the other projects I've worked on through my [LinkedIn page](https://www.linkedin.com/in/scottwalkerlondon). A few of the big government projects ([Carer's Allowance](https://github.com/Department-for-Work-and-Pensions/carers-allowance-prototype) and [DVLA](https://github.com/dvla)) are in the process of being open sourced.

Mind
====

Travis-CI build status for master branch [![Build Status](https://travis-ci.org/scottkwalker/Mind.svg?branch=master)](https://travis-ci.org/scottkwalker/Mind)

This is a Scala hobby project. It will consist of:

* one RESTful micro-service that will generate a table of legal moves in a programming language according to whatever rules you give it
* one micro-service that will call the first micro-service to get a list of legal moves and then use AI to pick the next move. I want to experiment with algorithms such as Ant Colony Optimisation (ACO).

It doesn't yet have the RESTful routes as there is plenty of work to do in the back end first.

Web framework
-------------
I am using [Play framework](http://www.playframework.com/documentation/2.3.x/Home) because:

* I am familiar with the framework from using it for projects for DWP Carer's Allowance and the DVLA.
* JSON writing and reading is done in what I find to be a very intuitive way ([JSON macro inception](http://www.playframework.com/documentation/2.2.3/ScalaJsonInception)).
* It offers the tools to create a rich front end.
* Although Spray has higher throughput it is very different to write the routes and I don't require throughput at the level.

Build server
------------
I am using [Travis CI](https://travis-ci.org/scottkwalker) as my build server because:

* It offers incredibly fast setup with a Github account.
* It is hosted in the cloud (unlike my previous Jenkins server that was running as a local instance).
* It automatically builds branches.
* It automatically builds forks!
* It is free.

Version control
---------------

I am using Git with Github because:

* I am familiar with Github from using it on several projects.
* I prefer using Git for version control as it works well with TDD's short iterations of coding. Also Git is great for local commits when I am working from a train with no 4G signal.