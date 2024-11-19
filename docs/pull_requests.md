# Pull requests

## Workflow

1. Once the change is assumed ready-to-merge, a pull request from the feature branch to the current stable branch is created.
2. One of the team members is set to be a **reviewer**; the author of the pull request is set to the **assignee**.
3. There can be several reviewers for a particular pull request. However, the assignee remains responsible for the changes made.
4. During the review process, there may be several iterations of the author's changes and the reviewer's comments. Use GitHub "Request review" button to notify the reviewer. If the process lasts longer than one round, use "Re-request review" button.
5. As soon as the changes are reviewed and pass the automated checks, the assignee becomes responsible for merging the changes and deleting the feature branch. No stale or outdated branches should remain.

## Providing description

Each pull request **must** have a decent description:

- the context of the change - circumstances which triggered the change;
- the issue to fix or the feature to implement - a description of what it is;
- the expected behavior after the change and optionally, some code snippets;
- (optional) dependency updates, increased versions of the framework, etc.

## Work-in-progress

In some cases, the PR is used to present an incomplete piece of code and discuss it with the team members. GitHub code review tool is then used as a flexible communication tool, allowing to keep the comments right next to the codebase.

If you are on early stages of the feature, and it’s really far from being ready for review, please may use [draft pull requests](https://github.blog/2019-02-14-introducing-draft-pull-requests/).